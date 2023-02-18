package httputil

import (
	"net"
	"net/http"
	"time"
)

var httpClient = http.DefaultClient

func init() {
	httpClient.Transport = &http.Transport{
		Dial: (&net.Dialer{
			Timeout: 5 * time.Second,
		}).Dial,
		TLSHandshakeTimeout: 5 * time.Second,
		Proxy:               http.ProxyFromEnvironment,
	}
	httpClient.Timeout = 10 * time.Second
}

func DefaultClient() *http.Client {
	return httpClient
}
