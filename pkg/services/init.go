package services

import (
	"context"
	"os"
	"os/signal"
	"time"

	"golang.org/x/exp/slog"
	"io.github.com/sks/services/pkg/logging"

	_ "io.github.com/sks/services/pkg/metrics"
	_ "io.github.com/sks/services/pkg/tracer"
)

func Init(ctx context.Context) (context.Context, func()) {
	ctx, cancel := signal.NotifyContext(ctx, os.Kill, os.Interrupt)
	startTime := time.Now()
	logging.GetLogger(ctx).Info("starting the server")
	return ctx, func() {
		logging.GetLogger(ctx).Warn("shutting down the server", slog.Float64("minutes", time.Since(startTime).Minutes()))
		cancel()
	}
}
