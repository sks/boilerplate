package handlers

import (
	"fmt"
	"net/http"
	"runtime"
	"runtime/debug"

	"golang.org/x/exp/slog"

	"io.github.com/sks/services/pkg/logging"
)

func RecoveryHandler(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		defer func() {
			if err := recover(); err != nil {
				_, file, num, _ := runtime.Caller(2)
				w.WriteHeader(http.StatusInternalServerError)
				logging.GetLogger(r.Context()).Warn("panic in request handling", slog.Any("error", err),
					slog.String("file", fmt.Sprintf("%s:%d", file, num)),
					slog.Any("stack", string(debug.Stack())))
			}
		}()
		next.ServeHTTP(w, r)
	})
}
