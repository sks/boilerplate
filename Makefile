BUILD_DIR := "build"
GIT_COMMIT = $(shell git rev-parse --short HEAD)
GIT_DIRTY  = $(shell test -n "`git status --porcelain`" && echo "-dirty" || echo "")
BRANCH=$(shell git rev-parse --abbrev-ref HEAD)
DOCKER_TAG=${GIT_COMMIT}${GIT_DIRTY}
DOCKER_REPO=sabithksme

mod:
	go mod tidy && go mod vendor

build/%:
	go build -o ./${BUILD_DIR}/$* ./cmd/$*/

build: build/mf-importjson

dockerize/%:
	docker build \
		--build-arg GIT_TAG=${DOCKER_TAG} \
		-t ${DOCKER_REPO}/$*:${DOCKER_TAG} \
		-f cmd/$*/Dockerfile .

dockerize: dockerize/mf-importjson