package importjson

import (
	"net/http"

	"github.com/sks/boilerplate/pkg/httputil"
)

type Options struct {
}

type Handler struct {
}

func (h Handler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	httputil.EncodeResponse(r.Context(), w, ImportmapOutput{
		Imports: DefaultMap(),
	})
	w.WriteHeader(http.StatusOK)
}

func NewHandler(opts Options) http.Handler {
	return Handler{}
}
