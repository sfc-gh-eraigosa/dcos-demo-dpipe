# Troubleshooting

## Reference sites
- https://dcos.io/docs/1.9/installing/troubleshooting/

## Subjects

* [Vagrant](#VAGRANT)
* [DC/OS](#DCOS)
* [Flink](#FLINK)
* [Kafka](#KAFKA)
* [General](#GENERAL)

# VAGRANT

## The url m1.dcos-demo doesn't come up in the browser

This might be caused due to bad /etc/hosts file.

Validate that `vagrant hostmanager` is able to run properly.  If errors
   occur correct the entries in /etc/hosts and try `vagrant hostmanager` to correct all entries.

# DCOS

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

## Where is the page that generates the auth token?

     The auth token page can be found here:

       http://m1.dcos-demo/login?redirect_uri=urn:ietf:wg:oauth:2.0:oob

# FLINK

# If flink fails to start

In the flink_scrips directory you can also find a flink-minimal.json configuration that can be used to verify that flink is functional from a startup perspective.  By default the demo will require approximately 2.5 cpus and 3G of memory to be offered from DC/OS.  To test the minimal configuration you can run the following command, in the `demo/flink_scripts` directory:

```
dcos package install flink --options=flink-minimal.json --yes
```

# KAFKA

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
     --topic fintrans'
```

Consume the message:
```
sudo docker run -it --rm mesosphere/kafka-client \
     ./kafka-console-consumer.sh \
      --zookeeper master.mesos:2181/dcos-service-kafka \
      --topic fintrans --from-beginning
```
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

## Recover broker after cluster restart

If your running a DC/OS environment on a small computer or laptop and you place the computer to sleep.  After starting the system back up, kafka and brokers might remain down.  You can use the DC/OS marathon UI to restart the kafka services but the broker might still be down.  Use these steps to recover the broker:

1. Verify the broker is still down:
   ```
   dcos kafka broker list    
    []
   ```
   Indicates there are no active brokers.
2. Restart the broker:
   ```
   dcos kafka broker restart 0
   [
  "broker-0__4d9aa712-969d-4be0-8874-726f4546beae"
   ]
   ```
3. Running kafka connection should verify the broker connection is available again.
   ```
   dcos kafka connection
    {
      "address": [
        "192.168.0.5:9427"
      ],
      "zookeeper": "master.mesos:2181/dcos-service-kafka",
      "dns": [
        "broker-0.kafka.mesos:9427"
      ],
      "vip": "broker.kafka.l4lb.thisdcos.directory:9092"
    }

   ```


# GENERAL

## What urls should work after install?

1. Exhibitor url : http://m1.dcos-demo:8181/exhibitor/v1/ui/index.html
2. DC/OS dashboard: http://m1.dcos-demo/

## Starting netcat for flink wordcount streaming demo:
If your doing the flink training demos and trying to learn how to use the wordcount demos, these can be started on your host as follows:
```
nc -l -p 40000 $(hostname)

```
