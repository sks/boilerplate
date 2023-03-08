package handlers

import (
	"fmt"
	"net/http"

	"github.com/sks/boilerplate/pkg/logging"
)

func RecoveryHandler(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		defer func() {
			if err := recover(); err != nil {
				w.WriteHeader(http.StatusInternalServerError)
				logging.GetLogger(r.Context()).Error("panic in request handling", fmt.Errorf("error : %+v", err))
			}
		}()
		next.ServeHTTP(w, r)
	})
}
