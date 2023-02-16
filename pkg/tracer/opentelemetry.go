package tracer

import (
	"context"
	"log"

	"go.opentelemetry.io/otel"

	"io.github.com/sks/services/pkg/logging"
	"io.github.com/sks/services/pkg/osutils"
)

func init() {
	initTracer()
}

func initTracer() {
	otel.SetErrorHandler(otel.ErrorHandlerFunc(func(err error) {
		logging.GetLogger(context.Background()).Error("error handling", err)
	}))
	tp, err := tracerProvider(osutils.GetOrDefault("JAEGER_TRACES", "http://localhost:14268/api/traces"))
	if err != nil {
		log.Fatal(err)
	}
	otel.SetTracerProvider(tp)
}
