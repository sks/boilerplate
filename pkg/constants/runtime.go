package constants

import (
	"os"
	"path/filepath"
)

var (
	svcName string
	envName string
)

func init() {
	svcName = serviceName()
	envName = environment()
}

func serviceName() string {
	if val := os.Getenv("SERVICE_NAME"); val != "" {
		return val
	}

	return filepath.Base(os.Args[0])
}

func ServiceName() string {
	return svcName
}

func Environment() string {
	return envName
}

func environment() string {
	val := os.Getenv("ENV_NAME")
	if val == "" {
		return "local"
	}
	return val
}
