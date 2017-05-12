#!/bin/bash

install_folder="/opt/dcos_install"
chmod 0600 "${install_folder}/genconf/ssh_key"

if [ ! -z "${http_proxy}" ]; then
     sed -i "s/^#http_proxy:.*/http_proxy: $(echo $http_proxy|sed 's#/#\\\/#g')/g" "${install_folder}/genconf/config.yaml"
     sed -i "s/^#use_proxy:.*/use_proxy: \'true\'/g" "${install_folder}/genconf/config.yaml"
fi
if [ ! -z "${https_proxy}" ]; then
     sed -i "s/^#https_proxy:.*/https_proxy: $(echo $https_proxy|sed 's#/#\\\/#g')/g" "${install_folder}/genconf/config.yaml"
fi
if [ ! -z "${no_proxy}" ]; then
     sed -i "s/^#no_proxy:.*/no_proxy:\n- \'$(echo $no_proxy|sed 's#/#\\\/#g')\'\n/#g')/g" "${install_folder}/genconf/config.yaml"
fi


cd "${install_folder}" && \
curl -O https://downloads.dcos.io/dcos/stable/dcos_generate_config.sh && \
chmod +x /opt/dcos_install/dcos_generate_config.sh && \
./dcos_generate_config.sh --genconf

echo "--> done generating dcos config ..."
