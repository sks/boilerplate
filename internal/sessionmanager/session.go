package sessionmanager

import "github.com/google/uuid"

type Session struct {
	ID                uuid.UUID `gorm:"primaryKey" json:"id"`
	Email             string    `json:"email"`
	EmailVerified     bool      `json:"emailVerified"`
	Name              string    `json:"name"`
	PreferredUsername string    `json:"preferredUsername"`
}
