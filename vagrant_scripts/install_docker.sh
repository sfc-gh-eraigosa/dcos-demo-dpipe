#!/bin/bash

sudo bash -c 'cat > /etc/yum.repos.d/docker.repo << REPO
[dockerrepo]
name=Docker Repository
baseurl=https://yum.dockerproject.org/repo/main/centos/7/
enabled=1
gpgcheck=1
gpgkey=https://yum.dockerproject.org/gpg
timeout=60
REPO'

sudo -i yum install docker-engine-1.13.1 docker-engine-selinux-1.13.1 -y && \
sudo -i service docker stop && \
sudo sed -i 's/^ExecStart=.*/ExecStart=\/usr\/bin\/dockerd \-\-storage\-driver\=overlay/g' /usr/lib/systemd/system/docker.service && \
sudo usermod -a -G docker vagrant && \
sudo systemctl enable docker && \
sudo service docker start
