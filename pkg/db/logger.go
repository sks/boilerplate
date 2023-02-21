package db

import (
	"context"
	"fmt"
	"time"

	"golang.org/x/exp/slog"
	"gorm.io/gorm/logger"
	"io.github.com/sks/services/pkg/logging"
)

type gormLogger struct {
	lvl logger.LogLevel
}

func (l *gormLogger) LogMode(lvl logger.LogLevel) logger.Interface {
	l.lvl = lvl
	return l
}

func (l *gormLogger) Info(ctx context.Context, msg string, data ...interface{}) {
	logging.GetLogger(ctx).Info(fmt.Sprintf(msg, data...))

}

func (l *gormLogger) Warn(ctx context.Context, msg string, data ...interface{}) {
	logging.GetLogger(ctx).Warn(fmt.Sprintf(msg, data...))
}

func (l *gormLogger) Error(ctx context.Context, msg string, data ...interface{}) {
	logging.GetLogger(ctx).Warn(fmt.Sprintf(msg, data...))
}

func (l *gormLogger) Trace(ctx context.Context, begin time.Time, fc func() (sql string, rowsAffected int64), err error) {
	sql, rowsAffected := fc()
	logAttrs := []any{
		slog.String("sql", sql),
		slog.Int64("rowsAffected", rowsAffected),
		slog.Duration("duration", time.Since(begin)),
	}
	if err != nil {
		logAttrs = append(logAttrs, slog.String(slog.ErrorKey, err.Error()))
	}
	logging.GetLogger(ctx).Debug("done with query", logAttrs...)
}
