package handlers

import "net/http"

type Middleware func(http.Handler) http.Handler

func Apply(next http.Handler, middlewares ...Middleware) http.Handler {
	for i := range middlewares {
		next = middlewares[i](next)
	}
	return next
}

func Default(next http.Handler) http.Handler {
	return Apply(next, RecoveryHandler, Log, Meter)
}
