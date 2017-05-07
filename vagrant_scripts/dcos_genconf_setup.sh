#!/bin/bash

chmod 0600 /opt/dcos_install/genconf/ssh_key


cd /opt/dcos_install && \
curl -O https://downloads.dcos.io/dcos/stable/dcos_generate_config.sh && \
chmod +x /opt/dcos_install/dcos_generate_config.sh && \
./dcos_generate_config.sh --genconf
