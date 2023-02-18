FROM golang:alpine as builder

ARG GIT_TAG

WORKDIR /code

COPY go.mod go.sum ./

RUN go mod vendor

COPY . .

RUN go build \
    -mod vendor \
    -ldflags="-X 'io.github.com/sks/services/pkg/constants.Version=${GIT_TAG}'" \
    -o /bin/session-manager \
    ./cmd/session-manager


FROM alpine

COPY --from=builder /bin/session-manager /bin/session-manager

ENTRYPOINT [ "/bin/session-manager" ]