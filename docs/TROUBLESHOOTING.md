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

## Login to the master node to check logs

We can login to the master node with the command:
  `vagrant ssh m1.dcos-demo`

From here several logs can be observed:

TBD

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
