package main

import (
	"context"
	"net/http"
	"os"

	"github.com/kelseyhightower/envconfig"
	"golang.org/x/exp/slog"
	"golang.org/x/sync/errgroup"

	"io.github.com/sks/services/internal/importjson"
	"io.github.com/sks/services/pkg/logging"
	"io.github.com/sks/services/pkg/serverutils"
	"io.github.com/sks/services/pkg/serverutils/handlers"
	"io.github.com/sks/services/pkg/services"
)

type appConfig struct {
	HTTPServer serverutils.HTTPServerOpts
	Options    importjson.Options
}

func main() {
	logger := logging.GetLogger(context.Background())

	var config appConfig
	err := envconfig.Process("", &config)
	if err != nil {
		logger.Error("error processing env config", err)
		os.Exit(1)
	}
	ctx, cancel := services.Init(context.Background())
	defer cancel()
	err = bootstrap(ctx, config)
	if err != nil {
		logger.Error("error bootstrapping app", err)
	}
}

func bootstrap(ctx context.Context, config appConfig) error {
	logger := logging.GetLogger(ctx)
	errGroup, ectx := errgroup.WithContext(ctx)
	errGroup.Go(func() error {
		logger.Info("starting http server", slog.String("port", config.HTTPServer.Port))
		serverMux := http.NewServeMux()
		serverMux.Handle("/importmap.json", importjson.NewHandler(config.Options))
		server := serverutils.NewServer(ectx, config.HTTPServer, handlers.Default(serverMux))
		return serverutils.KeepServerRunning(ectx, server)
	})

	return errGroup.Wait()
}
