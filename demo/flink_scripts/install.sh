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
echo "Installing minimal flink setup"
dcos auth login
dcos package install flink --yes
echo "--> wait 2 minutes"
sleep 120
