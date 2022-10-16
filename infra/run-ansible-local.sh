#!/bin/sh

cd "$(dirname "$0")"
export POSTGRES_DB_USER_PASSWORD="postgres_dummy";
export HNTOPLINKS_DB_USER_PASSWORD="hntoplinks_dummy";
export SENDGRID_API_KEY="dummy_api_key";
ansible-playbook -i ansible/local.yml --private-key utils/fedora-ssh/.vagrant/machines/default/virtualbox/private_key ansible/site.yml
