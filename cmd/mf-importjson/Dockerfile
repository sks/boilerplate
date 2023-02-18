FROM golang:alpine as builder

ARG GIT_TAG

WORKDIR /code

COPY go.mod go.sum ./

RUN go mod vendor

COPY . .

RUN go build \
    -ldflags="-X 'io.github.com/sks/services/pkg/constants.Version=${GIT_TAG}'" \
    -o /bin/mf-importjson \
    ./cmd/mf-importjson


FROM alpine

COPY --from=builder /bin/mf-importjson /bin/mf-importjson

ENTRYPOINT [ "/bin/mf-importjson" ]