#!/bin/bash

# setup environment files
echo "--> setup environment ..." && \
sudo bash -c 'cat > /etc/environment << ENV
export LC_ALL=en_US.utf-8
export LANG=en_US.utf-8
ENV'


# - allow passwordless sudoers
# - install minimam packages
# - setup ntp
# - disable dnsmasq

echo "--> minmal setup packages ..." && \
sudo bash -c "sed -i 's/^#\s%wheel\sALL=[(]ALL[)]\sNOPASSWD\:\sALL$/ %wheel\tALL=(ALL)\tNOPASSWD: ALL/g' /etc/sudoers" && \
sudo yum install curl bash iputils tar xz unzip ipset ntp ntpdate htop -y && \
echo "--> ntp setup ..." && \
sudo systemctl enable ntpd && \
sudo service ntpd stop && \
sudo ntpdate -s time.nist.gov && \
sudo service ntpd start && \
sudo ntptime && \
sleep 10 && \
date && \
ntpstat && \
echo "--> ntp is now setup ..." && \
echo "--> disable dnsmasq ..." && \
sudo systemctl disable dnsmasq && sudo systemctl stop dnsmasq;

# setup some extra utils
echo "--> setup extra packages ..." && \
sudo yum install bind-utils htop git vim -y

# setup the installer ssh keys
echo "--> setup authorized_keys ..."
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/id_rsa.pub ~/.ssh/authorized_keys

# make /opt/dcos_install/genconf/ip-detec executable
if [ -f /opt/dcos_install/genconf/ip-detect ]; then
  echo "--> make ip-detect executable ..."
  sudo chmod +x /opt/dcos_install/genconf/ip-detect
fi
