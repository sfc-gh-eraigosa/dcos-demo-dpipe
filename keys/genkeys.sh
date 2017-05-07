#!/bin/sh

rm -f ./id_rsa && rm -f ./id_rsa.pub && \
 ssh-keygen -t rsa -C 'vagrant@dcos-demo' -f ./id_rsa -P ''

echo "Keys are ready!"
