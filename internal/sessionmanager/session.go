package sessionmanager

type Session struct {
	Email             string `json:"email"`
	EmailVerified     bool   `json:"emailVerified"`
	Name              string `json:"name"`
	PreferredUsername string `json:"preferredUsername"`
}
