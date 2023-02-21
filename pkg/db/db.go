package db

import (
	"fmt"

	"gorm.io/gorm"
	"gorm.io/plugin/dbresolver"
)

type Configuration struct {
	ConnectionStr string   `envconfig:"DB_CONNECTION_STRING"`
	Replicas      []string `envconfig:"DB_REPLICAS"`
	DBDriver      string   `envconfig:"DB_TYPE" default:"postgres"`
}

func (c Configuration) ReplicaDialectors() []gorm.Dialector {
	val := make([]gorm.Dialector, len(c.Replicas))
	dialectors := make([]gorm.Dialector, len(c.Replicas))
	for i := range c.Replicas {
		dialectors[i] = c.dbOpener()("db3_dsn")
	}
	return val
}

func (c Configuration) Dialector() gorm.Dialector {
	return c.dbOpener()(c.ConnectionStr)
}

func (cfg Configuration) DB() (*gorm.DB, error) {
	db, err := gorm.Open(cfg.Dialector(), &gorm.Config{
		Logger: &gormLogger{},
	})
	if err != nil {
		return nil, fmt.Errorf("Error opening DB connection: %w", err)
	}
	if len(cfg.Replicas) == 0 {
		return db, nil
	}

	err = db.Use(dbresolver.Register(dbresolver.Config{
		Replicas:          cfg.ReplicaDialectors(),
		Policy:            dbresolver.RandomPolicy{},
		TraceResolverMode: true,
	}))

	return db, err
}
