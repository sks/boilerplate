variable "profile" {
    type = string
    default = "local"
}

variable "flags" {
    type = map(string)
    default = {
        "postgres": true
    }
}