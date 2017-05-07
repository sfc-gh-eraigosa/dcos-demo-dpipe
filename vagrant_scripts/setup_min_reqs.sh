#!/bin/bash

# setup environment files
sudo bash -c 'cat > /etc/environment << ENV
export LC_ALL=en_US.utf-8
export LANG=en_US.utf-8
ENV'


# - allow passwordless sudoers
# - install minimam packages
# - setup ntp
# - disable dnsmasq

sudo bash -c "sed -i 's/^#\s%wheel\sALL=[(]ALL[)]\sNOPASSWD\:\sALL$/ %wheel\tALL=(ALL)\tNOPASSWD: ALL/g' /etc/sudoers" && \
sudo yum install curl bash iputils tar xz unzip ipset ntp ntpdate htop -y && \
sudo ntptime && \
sudo systemctl enable ntpd && \
sudo service ntpd start && \
sudo service ntpd status && \
sudo ntpstat && \
date && \
sudo systemctl disable dnsmasq && sudo systemctl stop dnsmasq

# setup some extra utils
sudo yum install bind-utils htop git vim -y

# setup the installer ssh keys
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/id_rsa.pub ~/.ssh/authorized_keys

# make /opt/dcos_install/genconf/ip-detec executable
if [ -f /opt/dcos_install/genconf/ip-detect ]; then
  sudo chmod +x /opt/dcos_install/genconf/ip-detect
fi
