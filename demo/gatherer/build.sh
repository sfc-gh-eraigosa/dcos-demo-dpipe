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


"$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/build.containers.sh"

echo "build done"
