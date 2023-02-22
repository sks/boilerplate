package serverutils

import (
	"context"
	"errors"
	"fmt"
	"net"
	"net/http"
	"time"

	"io.github.com/sks/services/pkg/logging"
)

type HTTPServerOpts struct {
	Port              string        `envconfig:"PORT" default:"3000"`
	ReadTimeout       time.Duration `envconfig:"HTTP_READ_TIMEOUT" default:"1m"`
	ReadHeaderTimeout time.Duration `envconfig:"HTTP_READ_HEADER_TIMEOUT" default:"1m"`
	WriteTimeout      time.Duration `envconfig:"HTTP_WRITE_TIMEOUT" default:"1m"`
	IdleTimeout       time.Duration `envconfig:"HTTP_IDLE_TIMEOUT" default:"1m"`
}

func KeepServerRunning(ctx context.Context, server *http.Server) error {
	go func() {
		err := server.ListenAndServe()
		if !errors.Is(err, http.ErrServerClosed) {
			logging.GetLogger(ctx).Error("error starting server", err)
			panic(err)
		}
	}()
	<-ctx.Done()
	tctx, cancel := context.WithTimeout(context.Background(), 2*time.Minute)
	defer cancel()
	logging.GetLogger(ctx).Info("Shutting down the http.server")
	err := server.Shutdown(tctx)
	if err != nil {
		return fmt.Errorf("error shutting down server: %w", err)
	}
	return nil
}

func NewServer(ctx context.Context, opts HTTPServerOpts, handler http.Handler) *http.Server {
	return &http.Server{
		Addr:              net.JoinHostPort("", opts.Port),
		ReadTimeout:       opts.ReadTimeout,
		ReadHeaderTimeout: opts.ReadHeaderTimeout,
		WriteTimeout:      opts.WriteTimeout,
		IdleTimeout:       opts.IdleTimeout,
		BaseContext: func(l net.Listener) context.Context {
			return ctx
		},
		Handler: handler,
	}
}
