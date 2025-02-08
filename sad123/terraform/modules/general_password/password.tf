resource "random_password" "this" {
  length           = var.password_length
  special          = var.include_special
  override_special = var.override_special
  lower            = var.include_lowercase
  upper            = var.include_uppercase
  numeric          = var.include_numbers
  min_lower        = min(var.min_lowercase, 0)
  min_upper        = min(var.min_uppercase, 0)
  min_numeric      = min(var.min_numeric, 0)
  min_special      = min(var.min_special, 0)
}

output "value" {
  value     = random_password.this.bcrypt_hash
  sensitive = true
}
