# Lets generate the data

We'll use the DC/OS examples to create a small container that will generate
our data.

# Building it

We'll deploy a docker container similar to the steps in [these instructions](https://dcos.io/docs/1.9/deploying-services/creating-services/deploy-docker-app/).

1. Connect to the `bootstrap.dcos-demo` node:
   ```
   vagrant ssh bootstrap.dcos-demo
   ```
2. In order to push the container durring the build step, you should login.
   ```
   docker login
   ```

3. Now run the build.sh script to build and publish the container.

   ```
   ./build.sh
   ```

   **Notice**, you do not need to build this container if you are just deploying it, continue to the [Running it](#running-it) topic below.

4. Test that the container built ok : `docker run -it --rm data_generator --version`

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
