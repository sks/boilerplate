package berror

type IError interface {
	Message() string
	Type() string
	StatusCode() int
	Data() []any
}

type Error struct {
	M    string `json:"message"`
	T    string `json:"type"`
	Code int    `json:"-"`
	D    []any  `json:"data"`
}

func (e Error) Message() string {
	return e.M
}

func (e Error) Type() string {
	return e.T
}

func (e Error) StatusCode() int {
	return e.Code
}

func (e Error) Data() []any {
	return e.D
}

func New(msg string, errType string, statusCode int, data ...any) IError {
	return Error{
		M:    msg,
		T:    errType,
		Code: statusCode,
		D:    data,
	}
}
