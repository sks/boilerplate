package services

import (
	"context"
	"os"
	"os/signal"
	"time"

	"github.com/sks/boilerplate/pkg/logging"
	"golang.org/x/exp/slog"

	_ "github.com/sks/boilerplate/pkg/metrics"
	_ "github.com/sks/boilerplate/pkg/tracer"
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
