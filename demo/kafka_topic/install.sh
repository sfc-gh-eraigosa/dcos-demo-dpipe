#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

if ! dcos --version 2>/dev/null; then
  echo "Install a dcos cli!"
  exit 1
fi

# setup kafka
echo "Installing minimal kafka setup"
dcos auth login
dcos package install kafka --options=kafka-minimal.json --yes
echo "--> wait 2 minutes"
sleep 120


# setup topic
dcos kafka topic create fintrans --partitions 1 --replication 1
