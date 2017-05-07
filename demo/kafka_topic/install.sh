#!/bin/bash

if [ ! -x dcos ]; then
  echo "Install a dcos cli!"
  exit 1
fi
dcos auth login
dcos package install kafka --options=kafka-minimal.json


# setup topic
dcos kafka topic create topic1 --partitions 1 --replication 1
