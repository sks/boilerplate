package osutils

import "os"

func GetOrDefault(env, defaultVal string) string {
	val, ok := os.LookupEnv(env)
	if ok {
		return val
	}

	return defaultVal
}
