package httputil

import (
	"context"
	"encoding/json"
	"net/http"

	"github.com/sks/boilerplate/pkg/berror"
	"github.com/sks/boilerplate/pkg/logging"
	"github.com/sks/boilerplate/pkg/metrics"
)

func EncodeResponse(ctx context.Context, w http.ResponseWriter, val any) {
	err := json.NewEncoder(w).Encode(val)
	if err != nil {
		_ = metrics.RecordError(ctx, err, 2)
		logging.GetLogger(ctx).Error("error encoding response", err)
	}
}

func WriteError(ctx context.Context, w http.ResponseWriter, err error) {
	val, ok := err.(berror.IError)
	if !ok {
		_ = metrics.RecordError(ctx, err, 2)
		val = berror.New("internal server error", "INTERNAL_SERVER_ERROR", http.StatusInternalServerError)
	}
	_ = json.NewEncoder(w).Encode(val)
	w.WriteHeader(val.StatusCode())
}
