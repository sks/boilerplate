package sessionmanager

import (
	"context"
	"fmt"
	"net/http"

	"github.com/coreos/go-oidc/v3/oidc"
	"golang.org/x/oauth2"

	"io.github.com/sks/services/pkg/berror"
	"io.github.com/sks/services/pkg/httputil"
)

type Option struct {
	Client struct {
		ID          string `envconfig:"CLIENT_ID" default:"session-manager"`
		Secret      string `envconfig:"CLIENT_SECRET"`
		RedirectURL string `envconfig:"OIDC_REDIRECT_URL"`
	}
	IssuerURL string `envconfig:"ISSUER_URL" default:"http://kubernetes.docker.internal/sso"`
	Dex       struct {
		GRPCEndpoint string `envconfig:"DEX_GRPC_ENDPOINT" default:"dex-idp:5558"`
		CertLocation string `envconfig:"DEX_GRPC_CERT_LOCATION" `
	}
}

type Handler struct {
	opts     Option
	provider *oidc.Provider
	verifier *oidc.IDTokenVerifier
}

func NewSessionManagerHandler(ctx context.Context, opts Option) (Handler, error) {
	provider, err := oidc.NewProvider(ctx, opts.IssuerURL)
	if err != nil {
		return Handler{}, fmt.Errorf("error validating the issuer url: %w", err)
	}
	return Handler{
		opts:     opts,
		provider: provider,
		verifier: provider.Verifier(&oidc.Config{ClientID: opts.Client.ID}),
	}, nil
}

func (h Handler) Login(w http.ResponseWriter, req *http.Request) {
	redirectURL := h.oauth2Config(req.Context(), []string{"openid", "profile", "email"}).AuthCodeURL(req.URL.Query().Get("state"))
	http.Redirect(w, req, redirectURL, http.StatusTemporaryRedirect)
}

func (h Handler) Callback(w http.ResponseWriter, req *http.Request) {
	// Authorization redirect callback from OAuth2 auth flow.
	if errMsg := req.FormValue("error"); errMsg != "" {
		httputil.WriteError(req.Context(), w, berror.New(req.FormValue("error_description"), "BAD_OAUTH_CALLBACK", http.StatusBadRequest))
		return
	}
	code := req.FormValue("code")
	if code == "" {
		httputil.WriteError(req.Context(), w, berror.New("no code in request", "NO_OAUTH_CODE", http.StatusBadRequest))
		return
	}
	ctx := oidc.ClientContext(req.Context(), httputil.DefaultClient())
	oauth2Config := h.oauth2Config(req.Context(), nil)
	token, err := oauth2Config.Exchange(ctx, code)
	if err != nil {
		httputil.WriteError(req.Context(), w, err)
		return
	}
	_, err = h.verifyToken(req.Context(), token)
	if err != nil {
		httputil.WriteError(req.Context(), w, err)
		return
	}

}

func (h Handler) verifyToken(ctx context.Context, token *oauth2.Token) (Session, error) {
	rawIDToken, ok := token.Extra("id_token").(string)
	if !ok {
		return Session{}, berror.New("no id_token in token response", "NO_ID_TOKEN", http.StatusInternalServerError)
	}
	idToken, err := h.verifier.Verify(ctx, rawIDToken)
	if err != nil {
		return Session{}, berror.New("failed to verify ID token", "INVALID_TOKEN", http.StatusInternalServerError)
	}

	var session Session
	if err := idToken.Claims(&session); err != nil {
		return Session{}, fmt.Errorf("error decoding claims: %w", err)
	}
	return session, nil
}

func (h Handler) oauth2Config(ctx context.Context, scopes []string) *oauth2.Config {
	return &oauth2.Config{
		ClientID:     h.opts.Client.ID,
		ClientSecret: h.opts.Client.Secret,
		Endpoint:     h.provider.Endpoint(),
		Scopes:       scopes,
		RedirectURL:  h.opts.Client.RedirectURL,
	}
}
