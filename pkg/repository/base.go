package repository

import (
	"context"

	"gorm.io/gorm"
)

//go:generate go run github.com/maxbrunsfeld/counterfeiter/v6 -generate

//counterfeiter:generate . IBaseRepo
type IBaseRepo interface {
	DB(ctx context.Context) *gorm.DB
}

type BasePersistent struct {
	db *gorm.DB
}

func NewBase(db *gorm.DB) BasePersistent {
	return BasePersistent{
		db: db,
	}
}

func (b BasePersistent) DB(ctx context.Context) *gorm.DB {
	return b.db.WithContext(ctx)
}
