# Introduction

DC/OS as you can see is very easy to deploy, and can be deployed on [many cloud platforms](https://dcos.io/install/).

Our next exercise is to try a real demo.  DC/OS provides it's customers with best in class data analytics platform, and
this is definitely one of the Mesosphere Inc. strengths for data scientist.  This demo will focus on exercising a hypothetical data flow.

# Overview

We will work a simple data pipeline that allows us to solve a basic question.
The pipeline will best be represented by a 3 step flow.

```text
|------------------|     |---------------------|     |-------------------|
| A data generator | --> | A message queue     | --> | A consumer to     |
| to produce data  |     | to capture the data |     | analyze the data  |
|------------------|     |---------------------|     |-------------------|
```

These are the components that we will work with:
1. A simple go app in a container to generate and produce the data.
2. Kafka topics to capture the data in a message queue for later processing.
3. Flink analytics script to consume the message queue data and provide some
   real time analysis of the data to help us capture an answer to our problem.

# Requirements & Component Install

The components that need to be setup ahead of time include:
1. A docker engine so that we can [build and create the container image](data_generator/README.md).
2. A minimal install of [Kafka](https://github.com/dcos/examples/tree/master/kafka/1.9) to capture the data.
3. A minimal install of [Flink](https://github.com/dcos/examples/tree/master/flink/1.9) to analyze the data.

# This projects layout

+- [**demo**](README.md) - this folder
- +-- [**data_generator**](data_generator/README.md) - a folder with a simple [Dockerfile](data_generator/Dockerfile) to represent our data generator.
- +-- [**kafka_topic**](kafka_topic) - a folder to setup our kafka topic for the message queue.
- +-- [**flink_scripts**](flink_scripts) - a folder to help us analyze the incoming data

# Setup

To setup the demo, follow each README.md in the projects folders.  First setup
[**Kafka**](kafka_topic), then [**Flink**](flink_scripts), and finally the [**data generator**](data_generator/README.md).

Make sure to implement each component fully before moving on to the next.

# Problem

Financial averages often help us make decisions on how we invest our money.  Financial
institutions use complex data analytics to make daily price buy / sell decisions on equities.

Can we use DC/OS to help us know when we can buy or sell a given equity?

Lets use the data generator as a fake equity that is providing us with the current price of
some factitious equity.   The data will provide some random sampling that can help us
make some analysis of the data and predict when it might reach a buy/sell limit that we choose.

If the buy limit is reached, due to the equity approaching a low enough price, then we will allow
the consumer to let us know when to buy ahead of time, to get the best chance for a buy price.

If the sell limit is reached, due to the equity approaching a high enough price, then we will allow
the consumer to let us know when to sell the equity ahead of time for the best chance at getting the most for our purchase.

Get it?  Buy low, Sell high is what grandpa always told us.

# How to solve it


# Retrospective
