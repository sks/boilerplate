package serverutils

import (
	"context"
	"net/http"
	"time"

	"github.com/sks/boilerplate/pkg/logging"
)

func KeepServerRunning(ctx context.Context, server *http.Server) error {
	go func() {
		err := server.ListenAndServe()
		if err != nil && err != http.ErrServerClosed {
			logging.GetLogger(ctx).Error("error starting server", err)
			panic(err)
		}
	}()
	<-ctx.Done()
	tctx, cancel := context.WithTimeout(context.Background(), 2*time.Minute)
	defer cancel()
	logging.GetLogger(ctx).Info("Shutting down the http.server")
	return server.Shutdown(tctx)
}
