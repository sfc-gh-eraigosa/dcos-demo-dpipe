
# setup the plugins
required_plugins = %w(vagrant-proxyconf vagrant-vbguest vagrant-hostmanager)
required_plugins.each do | plugin |
  unless Vagrant.has_plugin? plugin
    puts "Installing missing plugin : #{plugin}"
    system "vagrant plugin install #{plugin}"
  end
end


$preconfig_bootstrap = <<SCRIPT
rm -rf /etc/docker/key.json;
yum update -y;
mkdir -p /opt/dcos_install/genconf && \
chown -R vagrant:vagrant /opt/dcos_install
chmod -R 775 /opt/dcos_install

SCRIPT

$preconfig_master = <<SCRIPT
rm -rf /etc/docker/key.json;
yum update -y;
SCRIPT

$preconfig_agent = <<SCRIPT
rm -rf /etc/docker/key.json;
yum update -y;
SCRIPT


Vagrant.configure(2) do |config|

  # vm defaults
  defaultbox = "bento/centos-7.3"
  dcosname = "dcos-demo"

  # configure vagrant-hostmanager plugin
  config.hostmanager.enabled = true
  config.hostmanager.manage_host = true
  config.hostmanager.ignore_private_ip = false

  # Avoid random ssh key for demo purposes
  config.ssh.insert_key = false
  # Vagrant Plugin Configuration: vagrant-vbguest
  if Vagrant.has_plugin?('vagrant-vbguest')
    # enable auto update guest additions
    config.vbguest.auto_update = true
  end


  config.vm.define "bootstrap.#{dcosname}" do |config|
    config.vm.box = defaultbox
    config.vm.hostname = "bootstrap.#{dcosname}"
    config.vm.network "private_network", ip: "192.168.0.2"
    config.vm.provider "virtualbox" do |vb|
         vb.name = "#{config.vm.hostname}"
         vb.memory = "1024"
         vb.cpus = 1
    end
  # Hack to remove loopback host alias that conflicts with vagrant-hostmanager
  # https://jira.mesosphere.com/browse/DCOS_VAGRANT-15
    config.vm.provision :shell, inline: "sed -i'' '/^127.0.0.1\\t#{config.vm.hostname}\\tbootstrap$/d' /etc/hosts"
    config.vm.provision "shell", inline: $preconfig_bootstrap
    config.vm.provision "file", source: "./genconf/ip-detect", destination: "/opt/dcos_install/genconf/ip-detect"
    config.vm.provision "file", source: "./genconf/config.yaml", destination: "/opt/dcos_install/genconf/config.yaml"
    config.vm.provision "file", source: "./keys/id_rsa", destination: "/opt/dcos_install/genconf/ssh_key"
    config.vm.provision "file", source: "./keys/id_rsa.pub", destination: "~/.ssh/id_rsa.pub"
    config.vm.provision "file", source: "./keys/id_rsa", destination: "~/.ssh/id_rsa"
    config.vm.provision "shell", path: "vagrant_scripts/setup_min_reqs.sh", privileged: false
    config.vm.provision "shell", path: "vagrant_scripts/install_docker.sh", privileged: true
    config.vm.provision "shell", path: "vagrant_scripts/dcos_genconf_setup.sh", privileged: true
    config.vm.synced_folder ".", "/vagrant", disabled: true
  end

  # configure masters
  config.vm.define "m1.#{dcosname}" do |config|
    config.vm.box = defaultbox
    config.vm.hostname = "m1.#{dcosname}"
    config.vm.network "private_network", ip: "192.168.0.3"
     # open these ports:  80, 443, 8080, 8181, 2181, 5050
    config.vm.network "forwarded_port", guest: 8080, host: 8080
    config.vm.network "forwarded_port", guest: 8181, host: 8181
    config.vm.network "forwarded_port", guest: 5050, host: 5050
    config.vm.network "forwarded_port", guest: 2181, host: 2181
    config.vm.network "forwarded_port", guest: 443, host: 443
    config.vm.network "forwarded_port", guest: 80, host: 80
    config.vm.provider "virtualbox" do |vb|
         vb.name = "#{config.vm.hostname}"
         vb.memory = "2048"
         vb.cpus = 2
    end
  # Hack to remove loopback host alias that conflicts with vagrant-hostmanager
  # https://jira.mesosphere.com/browse/DCOS_VAGRANT-15
    config.vm.provision :shell, inline: "sed -i'' '/^127.0.0.1\\t#{config.vm.hostname}\\tm1$/d' /etc/hosts"
    config.vm.provision "shell", inline: $preconfig_master
    config.vm.provision "file", source: "./keys/id_rsa.pub", destination: "~/.ssh/id_rsa.pub"
    config.vm.provision "shell", path: "vagrant_scripts/setup_min_reqs.sh", privileged: false
    config.vm.synced_folder ".", "/vagrant", disabled: true
  end

  # configure private agent 1
  config.vm.define "p1.#{dcosname}" do |config|
    config.vm.box = defaultbox
    config.vm.hostname = "p1.#{dcosname}"
    config.vm.network "private_network", ip: "192.168.0.4"
    config.vm.provider "virtualbox" do |vb|
         vb.name = "#{config.vm.hostname}"
         vb.memory = "4096"
         vb.cpus = 4
    end
  # Hack to remove loopback host alias that conflicts with vagrant-hostmanager
  # https://jira.mesosphere.com/browse/DCOS_VAGRANT-15
    config.vm.provision :shell, inline: "sed -i'' '/^127.0.0.1\\t#{config.vm.hostname}\\ta1$/d' /etc/hosts"
    config.vm.provision "shell", inline: $preconfig_agent
    config.vm.provision "file", source: "./keys/id_rsa.pub", destination: "~/.ssh/id_rsa.pub"
    config.vm.provision "shell", path: "vagrant_scripts/setup_min_reqs.sh", privileged: false
    config.vm.synced_folder ".", "/vagrant", disabled: true
  end

  # configure private agent 2
  config.vm.define "p2.#{dcosname}" do |config|
    config.vm.box = defaultbox
    config.vm.hostname = "p2.#{dcosname}"
    config.vm.network "private_network", ip: "192.168.0.5"
    config.vm.provider "virtualbox" do |vb|
         vb.name = "#{config.vm.hostname}"
         vb.memory = "4096"
         vb.cpus = 4
    end
  # Hack to remove loopback host alias that conflicts with vagrant-hostmanager
  # https://jira.mesosphere.com/browse/DCOS_VAGRANT-15
    config.vm.provision :shell, inline: "sed -i'' '/^127.0.0.1\\t#{config.vm.hostname}\\tp1$/d' /etc/hosts"
    config.vm.provision "shell", inline: $preconfig_agent
    config.vm.provision "file", source: "./keys/id_rsa.pub", destination: "~/.ssh/id_rsa.pub"
    config.vm.provision "shell", path: "vagrant_scripts/setup_min_reqs.sh", privileged: false
    config.vm.synced_folder ".", "/vagrant", disabled: true
  end

  # configure private agent 3
  config.vm.define "p3.#{dcosname}" do |config|
    config.vm.box = defaultbox
    config.vm.hostname = "p3.#{dcosname}"
    config.vm.network "private_network", ip: "192.168.0.6"
    config.vm.provider "virtualbox" do |vb|
         vb.name = "#{config.vm.hostname}"
         vb.memory = "4096"
         vb.cpus = 4
    end
  # Hack to remove loopback host alias that conflicts with vagrant-hostmanager
  # https://jira.mesosphere.com/browse/DCOS_VAGRANT-15
    config.vm.provision :shell, inline: "sed -i'' '/^127.0.0.1\\t#{config.vm.hostname}\\tp1$/d' /etc/hosts"
    config.vm.provision "shell", inline: $preconfig_agent
    config.vm.provision "file", source: "./keys/id_rsa.pub", destination: "~/.ssh/id_rsa.pub"
    config.vm.provision "shell", path: "vagrant_scripts/setup_min_reqs.sh", privileged: false
    config.vm.synced_folder ".", "/vagrant", disabled: true
  end

  # configure public agent 1
  config.vm.define "a1.#{dcosname}" do |config|
    config.vm.box = defaultbox
    config.vm.hostname = "a1.#{dcosname}"
    config.vm.network "private_network", ip: "192.168.0.7"
    config.vm.provider "virtualbox" do |vb|
         vb.name = "#{config.vm.hostname}"
         vb.memory = "1024"
         vb.cpus = 1
    end
  # Hack to remove loopback host alias that conflicts with vagrant-hostmanager
  # https://jira.mesosphere.com/browse/DCOS_VAGRANT-15
    config.vm.provision :shell, inline: "sed -i'' '/^127.0.0.1\\t#{config.vm.hostname}\\tp1$/d' /etc/hosts"
    config.vm.provision "shell", inline: $preconfig_agent
    config.vm.provision "file", source: "./keys/id_rsa.pub", destination: "~/.ssh/id_rsa.pub"
    config.vm.provision "shell", path: "vagrant_scripts/setup_min_reqs.sh", privileged: false
    config.vm.synced_folder ".", "/vagrant", disabled: true
  end
end
