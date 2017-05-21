# Lets generate the data

We'll use the DC/OS examples to create a small container that will generate
our data.

# Building it

We'll deploy a docker container similar to the steps in [these instructions](https://dcos.io/docs/1.9/deploying-services/creating-services/deploy-docker-app/).

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

4. Push the container to hub.docker.com registry so that we can deploy it.
   ```
   docker login
   # specify the user and password for the account
   docker tag data_generator <youraccount>/data_generator:latest
   docker push <youraccount>/data_generator:latest
   ```

   The service-generator.json script should be modified to use any new labels or container names.

To learn more about the fintrans generator, [visit the example here](https://github.com/dcos/demos/blob/master/1.9/fintrans/README.md).

# Pre-requisites

The data generator requires [Kafka to be setup first](../kafka_topic/README.md).

Once this is done, run the `topics.sh` script to create all required topics.

# Running it

Lets deploy this application as a normal container on our DC/OS cluster.

For this step, you'll need a working DC/OS environment and [dcos cli](../../docs/dcoscli.md).

Deploy the container service with DC/OS marathon
   ```
   ./install.sh
   ```

# Validate

Verify that we are getting data in one of more of the topics.  Topics will be city names such as : `London NYC SF Moscow Tokyo`.

1. Connect to one of the agent nodes.
   ```
   vagrant ssh p1.dcos-demo
   ```
2. Start a Kafka client container on one of the agents:
   ```
   docker run -it --rm mesosphere/kafka-client
   ```
3. Run a Kafka consumer script:
   ```
   ./kafka-console-consumer.sh --zookeeper 192.168.0.3:2181/dcos-service-kafka --topic "NYC"
   ```
