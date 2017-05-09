# Troubleshooting

## Refrence sites
- https://dcos.io/docs/1.9/installing/troubleshooting/

## What are the available nodes in this project?

You can find the viable nodes in the [Vagrantfile](../Vagrantfile), however
here is a way to get a list:
```
vagrant status
```

You can use those names to connect to specific dcos nodes.

## Date time might not be correct after snapshot restore

If you restored from a snapshot, it might be necessary to refresh the system time.

```
sudo systemctl enable ntpd && \
sudo service ntpd stop && \
sudo ntpdate -s time.nist.gov && \
sudo service ntpd start && \
sudo ntptime && \
sleep 10 && \
date && \
ntpstat
```

You can validate with dcos-navstar service that things are ok:

```
ENABLE_CHECK_TIME=true /opt/mesosphere/bin/check-time
```

## Login to the master node to check logs

We can login to the master node with the command:
  `vagrant ssh m1.dcos-demo`

From here several logs can be observed.  Use this command to run diagnostics:
```
sudo /opt/mesosphere/bin/./3dt -diag
```

Services not started are listed, you can then use commands like the following
to diagnose those services:

```
# lets say dcos-navstar.service.service is broken
/bin/systemctl status dcos-navstar.service
/bin/systemctl start  dcos-navstar.service

journalctl -xe
```

## List all running services on master nodes

We can list all the running services on the master node with this command:

```
```

# Troubleshooting installing marathon


# The url m1.dcos-demo doesn't come up in the browser

This might be caused due to bad /etc/hosts file.

Validate that `vagrant hostmanager` is able to run properly.  If errors
   occur correct the entries in /etc/hosts and try `vagrant hostmanager` to correct all entries.

# What urls should work after install?

1. Exhibitor url : http://m1.dcos-demo:8181/exhibitor/v1/ui/index.html
2. DC/OS dashboard: http://m1.dcos-demo/
3.
