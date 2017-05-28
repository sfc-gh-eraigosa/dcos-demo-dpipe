#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

set -x -v
function RunTask() {
  eval "flink run -m $FLINK_MASTER -c $FLINK_CLASS $FLINK_JAR $@ 2>&1"
  return $?
}

if [ ! "$1" = "--help" ]; then
  RunTask "$@"
  return $?
else
  echo "flink class runner accepts a list of arguments to run with a flink class"
fi
