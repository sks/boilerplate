package metrics

import (
	"context"
	"fmt"
	"runtime"

	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/metric"
	"go.opentelemetry.io/otel/metric/global"
	"go.opentelemetry.io/otel/metric/instrument"
	"golang.org/x/exp/slog"
	"io.github.com/sks/services/pkg/constants"
	"io.github.com/sks/services/pkg/logging"
)

var (
	meter       metric.Meter
	errRecorder instrument.Int64Counter
)

func getFrame(skipFrames int) runtime.Frame {
	// We need the frame at index skipFrames+2, since we never want runtime.Callers and getFrame
	targetFrameIndex := skipFrames + 2

	// Set size to targetFrameIndex+2 to ensure we have room for one more caller than we need
	programCounters := make([]uintptr, targetFrameIndex+2)
	n := runtime.Callers(0, programCounters)

	frame := runtime.Frame{Function: "unknown"}
	if n == 0 {
		return frame
	}
	frames := runtime.CallersFrames(programCounters[:n])
	for more, frameIndex := true, 0; more && frameIndex <= targetFrameIndex; frameIndex++ {
		var frameCandidate runtime.Frame
		frameCandidate, more = frames.Next()
		if frameIndex == targetFrameIndex {
			frame = frameCandidate
		}
	}
	return frame
}

func init() {
	meter = global.MeterProvider().Meter(constants.ServiceName())
	var err error
	errRecorder, err = meter.Int64Counter("server.error")
	if err != nil {
		logging.GetLogger(context.Background()).Error("error bootstrapping error counter", err)
	}
}

func RecordError(ctx context.Context, err error, tracesToSkip ...int) error {
	if err == nil {
		return nil
	}
	skip := 1
	if len(tracesToSkip) != 0 {
		skip = tracesToSkip[0]
	}
	frame := getFrame(skip)
	fileLoc := fmt.Sprintf("%s:%d", frame.File, frame.Line)
	logging.GetLogger(ctx).Error("recording the error", err, slog.String("frame", fileLoc))
	errRecorder.Add(ctx, 1,
		attribute.String("frame", fileLoc),
		attribute.String("error", err.Error()))
	return err
}
