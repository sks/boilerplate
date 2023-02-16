package main

import (
	"context"
	"net"
	"net/http"
	"os"
	"runtime"

	"io.github.com/sks/services/internal/importjson"
	"io.github.com/sks/services/pkg/logging"
	"io.github.com/sks/services/pkg/serverutils"
	"io.github.com/sks/services/pkg/serverutils/handlers"
	"io.github.com/sks/services/pkg/services"

	"github.com/kelseyhightower/envconfig"
	"golang.org/x/exp/slog"
	"golang.org/x/sync/errgroup"
)

type appConfig struct {
	Port    string `envconfig:"PORT" default:"3000"`
	Options importjson.Options
}

func main() {
	ctx, cancel := services.Init(context.Background())
	defer cancel()
	logger := logging.GetLogger(ctx)

	var config appConfig

	err := envconfig.Process("", &config)
	if err != nil {
		logger.Error("error processing env config", err)
		os.Exit(1)
	}
	logger.Info("starting the server", slog.String("port", config.Port))

	errGroup, ectx := errgroup.WithContext(ctx)
	errGroup.Go(func() error {
		serverMux := http.NewServeMux()
		serverMux.Handle("/importmap.json", importjson.NewHandler(config.Options))
		server := &http.Server{
			Addr: net.JoinHostPort("", config.Port),
			BaseContext: func(l net.Listener) context.Context {
				return ectx
			},
			Handler: handlers.Default(serverMux),
		}
		return serverutils.KeepServerRunning(ectx, server)
	})

	err = errGroup.Wait()
	if err != nil {
		logger.Error("server encountered an error", err)
		runtime.Goexit()
	}
}
