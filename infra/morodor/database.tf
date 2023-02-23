

resource "random_string" "database_admin_pwd" {
  length  = 32
  special = false
}

resource "random_string" "session_manager_db_pwd" {
  length  = 32
  special = false
}

resource "random_string" "sso_db_pwd" {
  length  = 32
  special = false
}

resource "kubernetes_secret" "db_init_script" {
  metadata {
    name      = "postgres-init-script"
    namespace = var.namespace
  }

  data = {
    "01_create_additional_database.sql" = templatefile("helm_values/db_postgres_init.sql", {
      databases = {
        "session_mgr" : random_string.session_manager_db_pwd.result,
        "sso" : random_string.sso_db_pwd.result,
      }
    })
  }
}

resource "helm_release" "database" {
  depends_on = [
    kubernetes_namespace.namespace,
    kubernetes_secret.db_init_script
  ]
  repository    = "https://charts.bitnami.com/bitnami"
  chart         = "postgresql-ha"
  namespace     = var.namespace
  wait_for_jobs = true
  reuse_values  = true
  reset_values  = true

  count   = (var.profile == "local") ? 1 : 0
  name    = "database"
  replace = true

  values = [
    templatefile("helm_values/database.yaml", {
      password            = random_string.database_admin_pwd.result,
      initdbScriptsSecret = "postgres-init-script"
    })
  ]
}