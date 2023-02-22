package logging

import (
	"context"
	"os"
	"path/filepath"

	"go.opentelemetry.io/otel/trace"
	"golang.org/x/exp/slog"

	"io.github.com/sks/services/pkg/constants"
)

var mylogger *slog.Logger

func init() {
	logHandler := slog.HandlerOptions{Level: slog.LevelInfo}
	if os.Getenv("DEBUG") != "" {
		logHandler.Level = slog.LevelDebug
	}
	mylogger = slog.New(logHandler.NewJSONHandler(os.Stderr)).
		With(
			slog.String("process", filepath.Base(os.Args[0])),
			slog.String("version", constants.Version))
}

func GetLogger(ctx context.Context) *slog.Logger {
	logger := mylogger.WithContext(ctx)
	spanContext := trace.SpanContextFromContext(ctx)
	if spanContext.HasSpanID() {
		logger = logger.With(slog.String("span", spanContext.SpanID().String()))
	}
	if spanContext.HasTraceID() {
		logger = logger.With(slog.String("trace", spanContext.TraceID().String()))
	}
	return logger
}
