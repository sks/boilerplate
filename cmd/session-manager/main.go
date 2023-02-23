package main

import (
	"context"
	"fmt"
	"net/http"
	"os"

	"github.com/kelseyhightower/envconfig"
	"golang.org/x/exp/slog"
	"golang.org/x/sync/errgroup"

	"io.github.com/sks/services/internal/sessionmanager"
	"io.github.com/sks/services/pkg/constants"
	"io.github.com/sks/services/pkg/db"
	"io.github.com/sks/services/pkg/logging"
	"io.github.com/sks/services/pkg/repository"
	"io.github.com/sks/services/pkg/serverutils"
	"io.github.com/sks/services/pkg/serverutils/handlers"
	"io.github.com/sks/services/pkg/services"
	"io.github.com/sks/services/pkg/svchealth"
)

type appConfig struct {
	HTTPServer           serverutils.HTTPServerOpts
	SessionManagerOption sessionmanager.Option
	DBConfig             db.Configuration
}

func main() {
	var config appConfig
	logger := logging.GetLogger(context.Background())
	err := envconfig.Process("", &config)
	if err != nil {
		logger.Error("error processing env config", err)
		os.Exit(1)
	}
	ctx, cancel := services.Init(context.Background())
	defer cancel()
	err = bootstrap(ctx, config)
	if err != nil {
		logger.Warn("bootstrapped failed", slog.Any("error", err))
	}
}

func bootstrap(ctx context.Context, config appConfig) error {
	logger := logging.GetLogger(ctx)
	db, err := config.DBConfig.DB()
	if err != nil {
		return fmt.Errorf("error connecting to DB: %w", err)
	}
	svchealth.RegisterHealthCheck(func(ctx context.Context) error {
		db, err := db.DB()
		if err == nil {
			err = db.PingContext(ctx)
		}
		if err != nil {
			return fmt.Errorf("error pinging DB: %w", err)
		}
		return nil
	})

	baseRepo := repository.NewBase(db)
	errGroup, ectx := errgroup.WithContext(ctx)
	sessionRepo := sessionmanager.NewSessionRepo(baseRepo)
	errGroup.Go(func() error {
		return sessionRepo.Migrate(ectx)
	})
	sessionHandler, err := sessionmanager.NewSessionManagerHandler(ctx, config.SessionManagerOption, sessionRepo)
	if err != nil {
		return fmt.Errorf("error creating session manager: %w", err)
	}
	errGroup.Go(func() error {
		return sessionHandler.CreateClientInDex(ectx)
	})
	errGroup.Go(func() error {
		logger.Info("starting http server", slog.String("port", config.HTTPServer.Port))
		serverMux := http.NewServeMux()
		defer logger.Info("stopped http server")
		serverMux.Handle(constants.HealthCheckEndpoint, svchealth.NewHandler())
		serverMux.HandleFunc("/auth/callback", sessionHandler.Callback)
		serverMux.HandleFunc("/auth/verify", sessionHandler.Verify)
		serverMux.HandleFunc("/auth/login", sessionHandler.Login)
		server := serverutils.NewServer(ectx, config.HTTPServer, handlers.Default(serverMux))
		return serverutils.KeepServerRunning(ectx, server)
	})

	err = errGroup.Wait()
	if err != nil {
		return fmt.Errorf("server encountered an error: %w", err)
	}
	return nil
}
