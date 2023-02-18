# Terraform Helm chart for morodor

## Commands

- [Makefile](./Makefile) is your friend
- `make apply/y` for quick turn around

Requires a file named `secrets.tfvars`, content looks something like this.

```sh
creds = {
    github_clientid: "<GITHUB_CLIENT_ID>"
    github_clientsecret: "<GITHUB_CLIENT_SECRET>"
}
```
