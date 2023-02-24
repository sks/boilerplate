

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

resource "kubernetes_secret" "db_connection_str" {
  metadata {
    name      = "db-connection-str"
    namespace = var.namespace
  }

  data = {
    "session_mgr" = format("host=database-postgresql-ha-pgpool user=session_mgr password=%s dbname=session_mgr port=5432 sslmode=disable", random_string.session_manager_db_pwd.result),
  }
}

resource "kubernetes_secret" "pool_passwd" {
  metadata {
    name      = "pool-passwd"
    namespace = var.namespace
  }

  type = "kubernetes.io/Opaque"
  data = {
    "usernames" = "session_mgr sso"
    "passwords" = format("%s %s", random_string.session_manager_db_pwd.result, random_string.sso_db_pwd.result)
  }
}

resource "helm_release" "database" {
  depends_on = [
    kubernetes_namespace.namespace,
    kubernetes_secret.db_init_script,
    kubernetes_secret.pool_passwd
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