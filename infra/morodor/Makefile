SECRETS_FILE="secrets.tfvars"

.DEFAULT=plan

install:
	which tfenv || brew install tfenv
	which terraform || (tfenv install 1.3.9 && tfenv use 1.3.9)
	touch ${SECRETS_FILE}

init: install
	terraform init

plan: init
	terraform plan --var-file=${SECRETS_FILE}

apply: plan
	terraform apply --var-file=${SECRETS_FILE}

apply/target/%:
	terraform apply --auto-approve -target=$* --var-file=${SECRETS_FILE}

plan/target/%:
	terraform plan  -target=$* --var-file=${SECRETS_FILE}

apply/y: plan
	terraform apply --auto-approve --var-file=${SECRETS_FILE}

destroy:
	terraform destroy --var-file=${SECRETS_FILE}

destroy/y:
	terraform destroy --auto-approve --var-file=${SECRETS_FILE}