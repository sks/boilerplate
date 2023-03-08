package sessionmanager

import (
	"context"
	"fmt"

	"github.com/dexidp/dex/api/v2"
	"github.com/sks/boilerplate/pkg/constants"
	"github.com/sks/boilerplate/pkg/logging"
	"golang.org/x/exp/slog"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
	"google.golang.org/grpc/credentials/insecure"
)

func (h Handler) newDexClient(hostAndPort, caPath string) (api.DexClient, *grpc.ClientConn, error) {
	grpcDialOptions := []grpc.DialOption{}
	if caPath != "" {
		creds, err := credentials.NewClientTLSFromFile(caPath, "")
		if err != nil {
			return nil, nil, fmt.Errorf("load dex cert: %w", err)
		}
		grpcDialOptions = append(grpcDialOptions, grpc.WithTransportCredentials(creds))
	} else {
		grpcDialOptions = append(grpcDialOptions, grpc.WithTransportCredentials(insecure.NewCredentials()))
	}

	conn, err := grpc.Dial(hostAndPort, grpcDialOptions...)
	if err != nil {
		return nil, nil, fmt.Errorf("dial: %w", err)
	}
	return api.NewDexClient(conn), conn, nil
}

func (h Handler) CreateClientInDex(ctx context.Context) error {
	client, conn, err := h.newDexClient(h.opts.Dex.GRPCEndpoint, h.opts.Dex.CertLocation)
	if err != nil {
		return fmt.Errorf("failed creating dex client: %w", err)
	}
	defer conn.Close()

	req := &api.CreateClientReq{
		Client: &api.Client{
			Id:           h.opts.Client.ID,
			Name:         constants.ServiceName(),
			Secret:       h.opts.Client.Secret,
			RedirectUris: []string{h.opts.Client.RedirectURL},
		},
	}
	logger := logging.GetLogger(ctx).With(slog.String("clientID", h.opts.Client.ID))
	resp, err := client.CreateClient(ctx, req)
	if err != nil {
		return fmt.Errorf("failed creating oauth2 client: %w", err)
	}
	if !resp.AlreadyExists {
		logger.Info("created oidc client", slog.Bool("alreadyExists", resp.AlreadyExists))
		return nil
	}
	logger.Info("updating the client")
	_, err = client.UpdateClient(ctx, &api.UpdateClientReq{
		Id:           h.opts.Client.ID,
		RedirectUris: []string{h.opts.Client.RedirectURL},
		Name:         constants.ServiceName(),
	})
	return err
}
