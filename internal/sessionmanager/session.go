package sessionmanager

import (
	"database/sql/driver"
	"encoding/json"
	"fmt"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

type Session struct {
	ID                uuid.UUID      `gorm:"primaryKey" json:"id"`
	Email             string         `json:"email"`
	EmailVerified     bool           `json:"email_verified"`
	Name              string         `json:"name"`
	PreferredUsername string         `json:"preferred_username"`
	FederatedClaim    federatedClaim `json:"federated_claims"`
}

type federatedClaim struct {
	*gorm.Model
	ConnectorID string `json:"connector_id"`
	UserID      string `json:"user_id"`
}

func (f *federatedClaim) Scan(src interface{}) error {
	return json.Unmarshal(src.([]byte), &f)
}

func (f federatedClaim) Value() (driver.Value, error) {
	val, err := json.Marshal(f)
	return val, err
}

func (f federatedClaim) String() string {
	return fmt.Sprintf("%s/%s", f.ConnectorID, f.UserID)
}
