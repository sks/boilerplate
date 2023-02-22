package httputil

import (
	"context"
	"encoding/json"
	"errors"
	"net/http"

	"golang.org/x/exp/slog"

	"io.github.com/sks/services/pkg/berror"
	"io.github.com/sks/services/pkg/logging"
	"io.github.com/sks/services/pkg/metrics"
)

func EncodeResponse(ctx context.Context, w http.ResponseWriter, val any) {
	err := json.NewEncoder(w).Encode(val)
	if err != nil {
		_ = metrics.RecordError(ctx, err, 2)
		logging.GetLogger(ctx).Error("error encoding response", err)
	}
}

func WriteError(ctx context.Context, w http.ResponseWriter, err error) {
	var val berror.IError = berror.Error{}
	ok := errors.As(err, &val)
	if !ok {
		_ = metrics.RecordError(ctx, err, 2)
		val = berror.New("internal server error", "INTERNAL_SERVER_ERROR", http.StatusInternalServerError)
	}
	err = json.NewEncoder(w).Encode(val)
	if err != nil {
		logging.GetLogger(ctx).Warn("error encoding error to writer", slog.Any("error", err))
	}
	w.WriteHeader(val.StatusCode())
}
