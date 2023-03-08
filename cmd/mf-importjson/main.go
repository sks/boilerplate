package main

import (
	"context"
	"net"
	"net/http"
	"os"
	"runtime"

	"github.com/sks/boilerplate/internal/importjson"
	"github.com/sks/boilerplate/pkg/logging"
	"github.com/sks/boilerplate/pkg/serverutils"
	"github.com/sks/boilerplate/pkg/serverutils/handlers"
	"github.com/sks/boilerplate/pkg/services"

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

	errGroup, ectx := errgroup.WithContext(ctx)
	errGroup.Go(func() error {
		logger.Info("starting http server", slog.String("port", config.Port))
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
