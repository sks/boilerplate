variable "kubeconfig" {
    default = "~/.kube/config"
    description = "kubernetes config to use"
}

variable "namespace" {
    default = "morodor"
    description = "Namespace to which resources are to be installed"
}

variable "domain" {
    default = "kubernetes.docker.internal"
    description = "domain on which the nginx ingress is listening"
}

variable "creds" {
    type = map(string)
    default = {}
}

variable "labels" {
    type = map(string)
    default = {
      "created_by" = "morodor_author"
      "git_owner" = "sks"
      "git_repo" = "boilerplate"
      "git_branch" = "main"
    } 
}