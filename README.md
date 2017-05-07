# Introduction

This is a simple demo for learning DC/OS installation and setup on local laptop.

If your looking for a more complex demo, checkout : https://github.com/dcos/dcos-vagrant.git

# How to prepare

1. (optional) trying to go passwordless?  Visit [these instructions](https://github.com/devopsgroup-io/vagrant-hostmanager#passwordless-sudo)
1. generate some keys if you havn't done that yet: `cd keys && ./genkeys.sh && cd ..`
1. run vagrant up!  `vagrant up`

# How to use

This is a manual install so we only prepared the minimum setup so that you can experience the steps for master and agents.

1. Connect to the bootstrap node: `vagrant ssh bootstrap.dcos-demo`
2. Change to dcos_install folder : `cd /opt/dcos_install`
3. Install all the node pre-reqs:  `sudo bash ./dcos_generate_config.sh --install-prereqs -v`
4. Verify we can install with pre-flights: `sudo bash ./dcos_generate_config.sh --preflight -v`
4. Deploy DC/OS: `sudo bash ./dcos_generate_config.sh --deploy -v`
5. Verify install is good with post-flights: `bash dcos_generate_config.sh --postflight -v`

# Trouble shoot

We can start with [this site](https://dcos.io/docs/1.9/installing/troubleshooting/).

# A real demo
Now lets do a [real demo](demo/README.md)!

# TODO cleanups
* Vagrantfile should be configurable for multinode setups
* make the ip assignment dynamic
