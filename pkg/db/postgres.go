package db

import (
	"context"

	"golang.org/x/exp/slog"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"io.github.com/sks/services/pkg/logging"
)

func (c Configuration) dbOpener() func(dsn string) gorm.Dialector {
	switch c.DBDriver {
	case "postgres":
		return postgres.Open
	}
	logging.GetLogger(context.Background()).Warn("unknown DB driver", slog.String("driver", c.DBDriver))
	return nil
}
