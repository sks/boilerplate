BUILD_DIR := "build"
GIT_COMMIT = $(shell git rev-parse --short HEAD)
GIT_DIRTY  = $(shell test -n "`git status --porcelain`" && echo "-dirty" || echo "")
BRANCH=$(shell git rev-parse --abbrev-ref HEAD)
DOCKER_TAG=${GIT_COMMIT}${GIT_DIRTY}

mod:
	go mod tidy && go mod vendor

build/%:
	go build \
		-ldflags="-X 'io.github.com/sks/services/pkg/constants.Version=${DOCKER_TAG}'" \
		-o ./${BUILD_DIR}/$* ./cmd/$*/

dockerize/%:
	docker build \
		--build-arg GIT_TAG=${DOCKER_TAG} \
		-t ${DOCKERHUB_USERNAME}/$*:${DOCKER_TAG} \
		-f cmd/$*/Dockerfile .

docker_push/%:
	$(MAKE) dockerize/$*
	docker push ${DOCKERHUB_USERNAME}/$*:${DOCKER_TAG}

helm/install/%:
	helm upgrade --wait --install $* ./cmd/$*/chart

helm/debug/%:
	helm upgrade --debug --dry-run --install $* ./cmd/$*/chart

tryout/%:
	@$(MAKE) dockerize/$*
	kubectl delete pod -l app.kubernetes.io/name=$*
	@$(MAKE) helm/install/$*

go/fakes:
	go generate ./...

go/lint:
	golangci-lint run ./...

go/test:
	go test ./...

go: go/fakes go/lint go/test