#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

docker build --tag wenlock/flink-pipe-runner:latest .
docker push wenlock/flink-pipe-runner:latest

echo "build containers done"
