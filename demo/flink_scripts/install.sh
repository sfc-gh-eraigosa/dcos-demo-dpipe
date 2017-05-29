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
if ! dcos node > /dev/null 2<&1; then
    dcos auth login
fi

dcos package install flink --options=flink-demo.json --yes
echo "--> wait 2 minutes"
sleep 120
