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
if ! dcos node > /dev/null 2<&1; then
    dcos auth login
fi

# generate service-generator.json
brokers=$(dcos kafka connection|jq -r '.dns|join(",")')
if [ -z "$brokers" ] ; then
    echo "ERROR : no brokers available to connect to. Deploy kafka or broker services first."
    exit 1
fi

zookeeper=$(dcos kafka connection|jq -r '.zookeeper')
if [ -z "$zookeeper" ] ; then
    echo "ERROR : no zookeeper connection available. Deploy kafka or broker services first."
    exit 1
fi

dcos_leader=$(dcos config show core.dcos_url)
if [ -z "$dcos_leader" ] ; then
    echo "ERROR : no DC/OS leader found, check dcos node command."
    exit 1
fi

flink_rpc_address=$(curl -s \
      --header "Authorization: token=$(dcos config show core.dcos_acs_token)" \
      --url "${dcos_leader}/service/dcos-demo/jobmanager/config" | \
      jq -r '.[]|select(.key | contains("jobmanager.rpc.address"))|.value')
if [ -z "$flink_rpc_address" ] ; then
    echo "ERROR : unable to get jobmanager.rpc.address jobmanager/config from flink. Check if flink is started."
    exit 1
fi

flink_rpc_port=$(curl -s \
      --header "Authorization: token=$(dcos config show core.dcos_acs_token)" \
      --url "${dcos_leader}/service/dcos-demo/jobmanager/config" | \
      jq -r '.[]|select(.key | contains("jobmanager.rpc.port"))|.value')
if [ -z "$flink_rpc_port" ] ; then
    echo "ERROR : unable to get jobmanager.rpc.port jobmanager/config from flink. Check if flink is started."
    exit 1
fi

topics="London NYC SF Moscow Tokyo"
for topic in $topics; do
  cat ./service-gatherer.json| \
       sed 's#@topic_id@#'$(echo $topic|sed -e 's#\(.*\)#\L\1#g')'#g' | \
       sed 's#@flink_master@#'$flink_rpc_address':'$flink_rpc_port'#g' | \
       sed 's#@brokers@#'$brokers'#g' | \
       sed 's#@zookeeper@#'$zookeeper'#g' | \
       sed 's#@topic@#'$topic'#g' \
       > "./.service-gatherer-${topic}.json"
  dcos marathon app add "./.service-gatherer-${topic}.json"
done

echo "--> wait 2 minutes"
sleep 120

# verify deployment status
dcos marathon app list
