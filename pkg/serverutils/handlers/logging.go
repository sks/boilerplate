package handlers

import (
	"net/http"
	"time"

	"golang.org/x/exp/slog"
	"io.github.com/sks/services/pkg/logging"
)

type statusAwareResponseWriter struct {
	http.ResponseWriter
	status int
}

func (s *statusAwareResponseWriter) WriteHeader(statusCode int) {
	s.status = statusCode
	s.ResponseWriter.WriteHeader(statusCode)
}

func Log(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		newStatusAwareResponseWriter := &statusAwareResponseWriter{w, 0}
		defer func(startTime time.Time) {
			logging.GetLogger(r.Context()).Debug("request handled",
				slog.Duration("duration", time.Since(startTime)),
				slog.Int("status", newStatusAwareResponseWriter.status),
				slog.String("path", r.URL.Path))
		}(time.Now())
		next.ServeHTTP(newStatusAwareResponseWriter, r)
	})
}
