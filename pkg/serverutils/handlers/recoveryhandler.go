package handlers

import (
	"net/http"

	"golang.org/x/exp/slog"

	"io.github.com/sks/services/pkg/logging"
)

func RecoveryHandler(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		defer func() {
			if err := recover(); err != nil {
				w.WriteHeader(http.StatusInternalServerError)
				logging.GetLogger(r.Context()).Warn("panic in request handling", slog.Any("error", err))
			}
		}()
		next.ServeHTTP(w, r)
	})
}
