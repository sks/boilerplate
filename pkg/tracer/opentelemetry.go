package tracer

import (
	"context"
	"log"

	"go.opentelemetry.io/otel"

	"github.com/sks/boilerplate/pkg/logging"
	"github.com/sks/boilerplate/pkg/osutils"
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
