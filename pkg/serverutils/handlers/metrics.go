package handlers

import (
	"net/http"

	"go.opentelemetry.io/contrib/instrumentation/net/http/otelhttp"

	"io.github.com/sks/services/pkg/constants"
)

func Meter(next http.Handler) http.Handler {
	return otelhttp.NewHandler(next, "http.server",
		otelhttp.WithFilter(func(r *http.Request) bool {
			return r.URL.Path != constants.HealthCheckEndpoint
		}),
		otelhttp.WithMessageEvents(otelhttp.ReadEvents, otelhttp.WriteEvents),
	)
}
