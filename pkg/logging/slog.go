package logging

import (
	"context"
	"os"
	"path/filepath"

	"github.com/sks/boilerplate/pkg/constants"

	"go.opentelemetry.io/otel/trace"
	"golang.org/x/exp/slog"
)

var mylogger *slog.Logger

func init() {
	logHandler := slog.HandlerOptions{Level: slog.LevelInfo}
	if os.Getenv("DEBUG") != "" {
		logHandler.Level = slog.LevelDebug
	}
	logger := slog.New(logHandler.NewJSONHandler(os.Stderr))
	mylogger = logger.With(slog.String("process", filepath.Base(os.Args[0])), slog.String("version", constants.Version))
}

func GetLogger(ctx context.Context) *slog.Logger {
	logger := mylogger
	spanContext := trace.SpanContextFromContext(ctx)
	if spanContext.HasSpanID() {
		logger = logger.With(slog.String("span", spanContext.SpanID().String()))
	}
	if spanContext.HasTraceID() {
		logger = logger.With(slog.String("trace", spanContext.TraceID().String()))
	}
	return logger
}
