#!/bin/bash

# setup the proxy
# If your using a proxy server, setup a proxy.env file in the current directory
# and source this script before running vagrant up
#
if [ ! -f ./proxy.env ]; then
  echo "SKIPPING proxy config, no proxy.env file in current directory"
  exit 0
fi
. ./proxy.env
echo "Proxy -> $PROXY"

if [ ! -z "$PROXY" ] &&  [ ! "$PROXY" = '"nil"' ]; then
    echo "setting up proxy"
    export http_proxy=http://$PROXY
    export https_proxy=https://$PROXY
    export HTTP_PROXY=$http_proxy
    export HTTPS_PROXY=$https_proxy
    export ftp_proxy=$(echo $http_proxy | sed 's/^http/ftp/g')
    export socks_proxy=$(echo $http_proxy | sed 's/^http/socks/g')
    export no_proxy="/var/run/docker.sock,localaddress,localhost,.dcos-demo,127.0.0.1,10.0.0.0/16,172.0.0.0/16,192.168.0.0/16,${no_proxy_domains}"
    export NO_PROXY=$no_proxy

else

    unset PROXY
    unset http_proxy
    unset https_proxy
    unset HTTP_PROXY
    unset HTTPS_PROXY
    unset ftp_proxy
    unset socks_proxy
    unset no_proxy
    unset NO_PROXY
    echo "un-setting proxy settings"
fi
