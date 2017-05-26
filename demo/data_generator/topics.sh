#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

function CreateTopics() {

    local topics=$1
    if [ -z "$topics" ]; then
        "ERROR, CreateTopics requires a list of topics argument"
        exit 1
    fi

    if ! dcos --version 2>/dev/null; then
      echo "Install a dcos cli!"
      exit 1
    fi

    # login
    echo "Authenticating with DC/OS"
    if ! dcos node > /dev/null 2<&1; then
        dcos auth login
    fi

    for topic in $topics; do
        if ! dcos kafka topic list|jq -r '.[]'|grep '^'$topic'$' > /dev/null; then
            echo "Creating topic ==> $topic"
            dcos kafka topic create $topic --partitions 1 --replication 1
        else
            echo "Skiping topic create, already exist, ==> $topic"
        fi
    done
}

CreateTopics "London NYC SF Moscow Tokyo movingaverage"
