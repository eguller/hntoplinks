#!/bin/sh


ansible-playbook -i ansible/local.yml --private-key utils/fedora-ssh/.ssh/id_rsa ansible/site.yml
