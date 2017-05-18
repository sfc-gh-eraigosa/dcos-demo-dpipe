#!/bin/bash

echo "--> configuring docker proxy settings for systemd ..."
if [ ! -d /etc/systemd/system/docker.service.d ]; then
    sudo mkdir -p /etc/systemd/system/docker.service.d
fi

source /etc/environment
if [ ! -z "${HTTP_PROXY}" ]; then
    sudo /bin/bash -c 'cat <<EOF > /etc/systemd/system/docker.service.d/proxy.conf
[Service]
EnvironmentFile=-/etc/sysconfig/docker
EnvironmentFile=-/etc/sysconfig/docker-storage
EnvironmentFile=-/etc/sysconfig/docker-network
Environment="HTTP_PROXY=$http_proxy" \
"HTTPS_PROXY=$https_proxy" \
"NO_PROXY=$no_proxy" \
"http_proxy=$http_proxy" \
"https_proxy=$https_proxy" \
"no_proxy=$no_proxy"
EOF'

    sudo systemctl daemon-reload
    sudo systemctl restart docker
else
    echo "--> skipping proxy config due to HTTP_PROXY being empty ..."
fi

echo "--> docker proxy configuration complete ..."
