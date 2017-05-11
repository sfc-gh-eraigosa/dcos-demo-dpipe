# Troubleshooting

## Reference sites
- https://dcos.io/docs/1.9/installing/troubleshooting/

## What are the available nodes in this project?

You can find the viable nodes in the [Vagrantfile](../Vagrantfile), however
here is a way to get a list:
```
vagrant status
```

or

```
dcos node
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
Learn more about navigating logs [here](https://dcos.io/docs/1.8/administration/installing/custom/troubleshooting/#-a-name-component-logs-a-component-logs).

## List all running services on master nodes

We can list all the running services on the master node with this command:

```
dcos service
```

## Working a kafka example

If your worried about kafka working, trying doing the kafka example:

https://github.com/dcos/examples/tree/master/kafka/1.9

Connect to the dcos master: `vagrant ssh m1.dcos-demo`

Produce a message:
```
sudo docker run -it --rm mesosphere/kafka-client bash -c ' \
     echo "Hello, World." | \
     ./kafka-console-producer.sh \
     --broker-list broker-0.kafka.mesos:9588 \
     --topic fintans'
```

Consume the message:
```
sudo docker run -it --rm mesosphere/kafka-client \
     ./kafka-console-consumer.sh \
      --zookeeper master.mesos:2181/dcos-service-kafka \
      --topic fintrans --from-beginning
```

## The url m1.dcos-demo doesn't come up in the browser

This might be caused due to bad /etc/hosts file.

Validate that `vagrant hostmanager` is able to run properly.  If errors
   occur correct the entries in /etc/hosts and try `vagrant hostmanager` to correct all entries.

## What urls should work after install?

1. Exhibitor url : http://m1.dcos-demo:8181/exhibitor/v1/ui/index.html
2. DC/OS dashboard: http://m1.dcos-demo/

## clean up kafka and all it's resources

1. remove the kafka service:
```
dcos package uninstall kafka
```
2. login to the master node and remove all zookeeper resources:
```
docker run mesosphere/janitor /janitor.py -r kafka-role -p kafka-principal -z dcos-service-kafka
```
Also see [these docs](https://docs.mesosphere.com/1.9/deploying-services/uninstall/#framework-cleaner) for more details


## IPv4 forwarding is disabled errors

You might get an error as follows:

```
vagrant@bootstrap dcos_install]$ sudo bash ./dcos_generate_config.sh --install-prereqs -v
WARNING: IPv4 forwarding is disabled. Networking will not work.
Logger set to DEBUG
====> EXECUTING INSTALL PREREQUISITES
====> START install_prereqs
====> STAGE install_prereqs
====> STAGE install_prereqs
====> STAGE install_prereqs
====> STAGE install_prereqs
====> STAGE install_prereqs
====> OUTPUT FOR install_prereqs
====> 192.168.0.4:22 FAILED
     TASK:
/usr/bin/ssh -oConnectTimeout=10 -oStrictHostKeyChecking=no -oUserKnownHostsFile=/dev/null -oBatchMode=yes -oPasswordAuthentication=no -p22 -i genconf/ssh_key -tt vagrant@192.168.0.4 echo INSTALL PREREQUISITES
```

There is a workaround for this documented on [stack overflow](http://stackoverflow.com/questions/41453263/docker-networking-disabled-warning-ipv4-forwarding-is-disabled-networking-wil)

- edit `/etc/sysctl.conf` and add `net.ipv4.ip_forward=1`
- restar network services:
  ```
  systemctl restart network
  sysctl net.ipv4.ip_forward
  ```
