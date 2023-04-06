package httputil

import (
	"encoding/json"
	"io"
)

func DecodeRequest(body io.ReadCloser, val any) error {
	return json.NewDecoder(body).Decode(val)
}
