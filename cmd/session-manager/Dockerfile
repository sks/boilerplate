FROM golang:alpine as builder

ARG GIT_TAG

WORKDIR /code

COPY go.mod go.sum ./

RUN go mod download

COPY . .

RUN go build \
    -ldflags="-X 'io.github.com/sks/services/pkg/constants.Version=${GIT_TAG}'" \
    -o /bin/session-manager \
    ./cmd/session-manager


FROM alpine

COPY --from=builder /bin/session-manager /bin/session-manager

CMD [ "/bin/session-manager" ]