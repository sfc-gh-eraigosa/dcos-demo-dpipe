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
echo "Installing data_generator service"
dcos auth login
dcos marathon app add service-generator.json --yes
echo "--> wait 2 minutes"
sleep 120


# verify deployment status
dcos app list
