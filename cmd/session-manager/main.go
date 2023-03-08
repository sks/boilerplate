package main

import (
	"context"
	"net"
	"net/http"
	"os"

	"github.com/kelseyhightower/envconfig"
	"github.com/sks/boilerplate/internal/sessionmanager"
	"github.com/sks/boilerplate/pkg/logging"
	"github.com/sks/boilerplate/pkg/serverutils"
	"github.com/sks/boilerplate/pkg/serverutils/handlers"
	"github.com/sks/boilerplate/pkg/services"
	"golang.org/x/exp/slog"
	"golang.org/x/sync/errgroup"
)

type appConfig struct {
	Port                 string `envconfig:"PORT" default:"3001"`
	SessionManagerOption sessionmanager.Option
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

	sessionHandler, err := sessionmanager.NewSessionManagerHandler(ctx, config.SessionManagerOption)
	if err != nil {
		logger.Error("error creating session manager", err)
		os.Exit(1)
	}
	errGroup, ectx := errgroup.WithContext(ctx)
	errGroup.Go(func() error {
		return sessionHandler.CreateClientInDex(ectx)
	})
	errGroup.Go(func() error {
		logger.Info("starting http server", slog.String("port", config.Port))
		serverMux := http.NewServeMux()
		defer logger.Info("stopped http server")
		serverMux.HandleFunc("/auth/callback", sessionHandler.Callback)
		serverMux.HandleFunc("/auth/login", sessionHandler.Login)
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
		os.Exit(1)
	}
}
