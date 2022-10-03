#!/bin/sh

script_dir="$(dirname "${BASH_SOURCE}")"

cd $script_dir
mkdir -p .ssh

if [ ! -f $script_dir/.ssh/id_rsa ] &&  [ ! -f $script_dir/.ssh/id_rsa.pub ]; then
  ssh-keygen -q -t rsa -N '' -f $script_dir/.ssh/id_rsa <<<y >/dev/null 2>&1
  cat $script_dir/.ssh/id_rsa.pub > .ssh/authorized_keys
else
  echo "Private/public key already exists, will not be generated again."
fi

chmod  600 $script_dir/.ssh/id_rsa

docker build . -t fedora-ssh-enabled:latest

docker run \
--rm \
-dit \
--name fedora-ssh-enabled \
--publish 9922:22 \
fedora-ssh-enabled:latest


