
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

# patch IPv4
if ! grep 'net.ipv4.ip_forward=1' /etc/sysctl.conf; then
  echo 'net.ipv4.ip_forward=1' | sudo tee -a /etc/sysctl.conf
fi
sudo systemctl restart network

SCRIPT

$preconfig_master = <<SCRIPT
rm -rf /etc/docker/key.json;
yum update -y;
SCRIPT

$preconfig_agent = <<SCRIPT
rm -rf /etc/docker/key.json;
yum update -y;
SCRIPT

# generic box
def dcos_box(name, domain, memory, cpus, ip, box, proxy, config)
  # configure private agent 3
  config.vm.define "#{name}.#{domain}" do |config|
    config.vm.box = box
    config.vm.hostname = "#{name}.#{domain}"
    config.vm.network "private_network", ip: ip
    config.vm.provider "virtualbox" do |vb|
         vb.name = "#{config.vm.hostname}"
         vb.memory = memory
         vb.cpus = cpus
    end

    if Vagrant.has_plugin?('vagrant-proxyconf') && proxy[:http_proxy] != ''
      config.proxy.http      = proxy[:http_proxy]
      config.proxy.https     = proxy[:https_proxy]
      config.proxy.no_proxy  = proxy[:no_proxy]
      config.yum_proxy.http  = proxy[:http_proxy]
      config.apt_proxy.http  = proxy[:http_proxy]
      config.apt_proxy.https = proxy[:https_proxy]
      config.proxy.enabled = true
    end

  # Hack to remove loopback host alias that conflicts with vagrant-hostmanager
  # https://jira.mesosphere.com/browse/DCOS_VAGRANT-15
    config.vm.provision :shell, inline: "sed -i'' '/^127.0.0.1\\t#{config.vm.hostname}\\tp1$/d' /etc/hosts"
    config.vm.provision "file", source: "./keys/id_rsa.pub", destination: "~/.ssh/id_rsa.pub"
    config.vm.synced_folder ".", "/vagrant", disabled: true
  end
  config
end

# a master box
def dcos_master(name, domain, memory, cpus, ip, box, proxy, config)
  config = dcos_box(name, domain, memory, cpus, ip, box, proxy, config)
  config.vm.define "#{name}.#{domain}" do |config|
    # open these ports:  80, 443, 8080, 8181, 2181, 5050
    config.vm.network "forwarded_port", guest: 8080, host: 8080
    config.vm.network "forwarded_port", guest: 8181, host: 8181
    config.vm.network "forwarded_port", guest: 5050, host: 5050
    config.vm.network "forwarded_port", guest: 2181, host: 2181
    config.vm.network "forwarded_port", guest: 443, host: 443
    config.vm.network "forwarded_port", guest: 80, host: 80

    config.vm.provision "shell", inline: $preconfig_master
    config.vm.provision "shell", path: "vagrant_scripts/setup_min_reqs.sh", privileged: false
  end
  config
end

# an agent box
def dcos_agent(name, domain, memory, cpus, ip, box, proxy, config)
  config = dcos_box(name, domain, memory, cpus, ip, box, proxy, config)
  config.vm.define "#{name}.#{domain}" do |config|
    config.vm.provision "shell", inline: $preconfig_agent
    config.vm.provision "shell", path: "vagrant_scripts/setup_min_reqs.sh", privileged: false
  end
  config
end

Vagrant.configure(2) do |config|

  # vm defaults
  defaultbox = "bento/centos-7.3"
  dcosname = "dcos-demo"

  proxy = {
    http_proxy: ENV['http_proxy'],
    https_proxy: ENV['https_proxy'],
    no_proxy: ENV['no_proxy']
  }

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

  # configure bootstrap
  config = dcos_box("bootstrap", dcosname, 1024, 1, "192.168.0.2", defaultbox, proxy, config)
  config.vm.define "bootstrap.#{dcosname}" do |config|
    config.vm.provision "shell", inline: $preconfig_bootstrap
    config.vm.provision "file", source: "./genconf/ip-detect", destination: "/opt/dcos_install/genconf/ip-detect"
    config.vm.provision "file", source: "./genconf/config.yaml", destination: "/opt/dcos_install/genconf/config.yaml"
    config.vm.provision "file", source: "./keys/id_rsa", destination: "/opt/dcos_install/genconf/ssh_key"
    config.vm.provision "file", source: "./keys/id_rsa", destination: "~/.ssh/id_rsa"
    config.vm.provision "shell", path: "vagrant_scripts/setup_min_reqs.sh", privileged: false
    config.vm.provision "shell", path: "vagrant_scripts/install_docker.sh", privileged: true
    config.vm.provision "shell", path: "vagrant_scripts/dcos_genconf_setup.sh", privileged: true
  end

  # configure masters
  config = dcos_master("m1", dcosname, 2048, 2, "192.168.0.3", defaultbox, proxy, config)
  # configure agents
  config = dcos_agent("p1", dcosname, 5120, 4, "192.168.0.4", defaultbox, proxy, config)
  config = dcos_agent("p2", dcosname, 5120, 4, "192.168.0.5", defaultbox, proxy, config)
  config = dcos_agent("p3", dcosname, 5120, 4, "192.168.0.6", defaultbox, proxy, config)
  config = dcos_agent("a1", dcosname, 2048, 1, "192.168.0.7", defaultbox, proxy, config)

end
