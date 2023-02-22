package svchealth

import (
	"context"
	"net/http"

	"golang.org/x/exp/slog"
	"golang.org/x/sync/errgroup"

	"io.github.com/sks/services/pkg/httputil"
	"io.github.com/sks/services/pkg/logging"
)

type HealthCheckFn func(ctx context.Context) error

type handler struct {
	checks []HealthCheckFn
}

var h *handler

func init() {
	h = &handler{
		checks: []HealthCheckFn{},
	}
}

func NewHandler() http.Handler {
	return h
}

func RegisterHealthCheck(f HealthCheckFn) {
	h.checks = append(h.checks, f)
}

func (h *handler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	errGroup, ectx := errgroup.WithContext(r.Context())
	for i := range h.checks {
		v := h.checks[i]
		errGroup.Go(func() error {
			return v(ectx)
		})
	}
	err := errGroup.Wait()
	if err != nil {
		logging.GetLogger(r.Context()).Error("health check failed", err)
		httputil.WriteError(r.Context(), w, err)
		return
	}
	if len(h.checks) != 0 {
		logging.GetLogger(r.Context()).Debug("health check passed", slog.Int("healtchecks", len(h.checks)))
	}
	w.WriteHeader(http.StatusOK)
}
