#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

# For debugging purposes, lets just start a simple top command
# to keep the container running
if [ "${KEEP_UP}" = "true" ] ; then
    if [ "${KEEP_UP_TIMEOUT}" = 0 ] ; then
        echo "Starting up dummy container for debugging ..."
        tail -f /dev/null
    else
        echo "Starting up dummy container for debugging ... timout is ${KEEP_UP_TIMEOUT}"
        timeout ${KEEP_UP_TIMEOUT} tail -f /dev/null
    fi
    exit 1
fi

echo "current /etc/hosts"
cat /etc/hosts

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
