# Lets generate the data

We'll use the DC/OS examples to create a small container that will generate
our data.

# Building it

1. Lets setup a local docker engine so we can build the images
   ```
   docker-machine create --driver virtualbox \
      --virtualbox-boot2docker-url https://github.com/boot2docker/boot2docker/releases/download/v1.13.1/boot2docker.iso \
      --virtualbox-memory "2048" \
      --virtualbox-disk-size "50000" \
      default
   eval $(docker-machine env)
   ```

2. Now we can build it as follows: `docker build --tag data_generator .`

3. Test that the container built ok : `docker run -it --rm data_generator --version`

To learn more about the fintrans generator, [visit the example here](https://github.com/dcos/demos/blob/master/1.9/fintrans/README.md).

# Running it

Lets deploy this application as a normal container on our DC/OS cluster.

For this step, you'll need a working DC/OS environment and dcos cli.
If you do not have a cli setup yet, you can do that [here](https://dcos.io/docs/1.9/cli/install/).

1. Export your built container from your docker-machine environment and import it into the DC/OS environment.
2. Deploy a single container service into DC/OS
3. Check the status
