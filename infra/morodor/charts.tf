provider "helm" {
  kubernetes {
    config_path = var.kubeconfig
  }
}

provider "kubernetes" {
  config_path = var.kubeconfig
}


resource "kubernetes_namespace" "namespace" {
  metadata {
    annotations = {
      name = var.namespace
    }

    labels = merge(var.labels, {
      created_by = "terraform_charts"
    })

    name = var.namespace
  }
}


resource "helm_release" "dex_release" {
  name      = "dex-idp"
  namespace = var.namespace

  repository = "https://charts.dexidp.io"
  chart      = "dex"
  replace    = true

  depends_on = [
    helm_release.nginx_controller,
    kubernetes_namespace.namespace
  ]

  values = [
    templatefile("helm_values/sso.yaml", {
      domain = var.domain
      creds  = var.creds
    })
  ]

  set {
    name  = "config.storage.type"
    value = "memory"
  }
}

resource "helm_release" "nginx_controller" {
  name      = "nginx-stable"
  namespace = var.namespace
  depends_on = [
    kubernetes_namespace.namespace,    
  ]

  repository = "https://helm.nginx.com/stable"
  chart      = "nginx-ingress"
  replace    = true
  values = [
    templatefile("helm_values/ingress.yaml", {
      domain = var.domain
    })
  ]
}

resource "kubernetes_ingress_v1" "master_ingress" {
  depends_on = [
    helm_release.nginx_controller,    
  ]
  metadata {
    name = "ingress-master"
    namespace = var.namespace
    annotations = {
      "nginx.org/mergeable-ingress-type" : "master"
    }
  }
  spec {
    ingress_class_name = "nginx"
    rule {
      host = var.domain
    }
  }
}

resource "helm_release" "jaeger" {
  name      = "jaeger-all-in-one"
  namespace = var.namespace

  depends_on = [
    kubernetes_namespace.namespace
  ]

  repository = "https://raw.githubusercontent.com/hansehe/jaeger-all-in-one/master/helm/charts"
  chart      = "jaeger-all-in-one"
  replace    = true

  values = [
    templatefile("helm_values/jaeger.yaml", {
      domain = var.domain
    })
  ]
}
