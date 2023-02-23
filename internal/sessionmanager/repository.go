package sessionmanager

import (
	"context"
	"fmt"

	"io.github.com/sks/services/pkg/repository"
)

type IRepo interface {
	repository.IBaseRepo
	Migrate(ctx context.Context) error
}

type Repository struct {
	repository.IBaseRepo
}

func NewSessionRepo(baseRepo repository.IBaseRepo) Repository {
	return Repository{
		baseRepo,
	}
}

func (s Repository) Migrate(ctx context.Context) error {
	err := s.DB(ctx).AutoMigrate(&Session{})
	if err != nil {
		return fmt.Errorf("error migrating session DB: %w", err)
	}
	return nil
}
