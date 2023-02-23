%{ for db_name, db_password in databases ~}CREATE USER ${db_name} PASSWORD '${db_password}';
CREATE DATABASE ${db_name} OWNER ${db_name};
%{ endfor ~}