#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

[ ! -d ./target ] && mkdir -p ./target
[ ! -d ./.m2 ] && mkdir -p ./.m2
docker run -it --rm \
            --workdir /workspace \
            -v $(pwd):/workspace \
            -v $(pwd)/.m2:/root/.m2 \
            maven \
            mvn clean package

docker build --tag wenlock/flink-pipe-runner:latest .
docker push wenlock/flink-pipe-runner:latest

echo "build done"
