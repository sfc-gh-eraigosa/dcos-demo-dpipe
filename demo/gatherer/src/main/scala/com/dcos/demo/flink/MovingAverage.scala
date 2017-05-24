package com.dcos.demo.flink

import java.util.Properties

import com.dcos.demo.flink.SocketWindowWordCount.WordWithCount
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.scala._
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010,FlinkKafkaProducer010}
import org.apache.flink.streaming.util.serialization.SimpleStringSchema

/**
  * Created by eraigosa on 5/23/17.
  * We're working on a demo to calculate the current moving average, and make a 10% future +/- guess at what the next value
  * will be.
  *
  * Lets accumulate an average over a time period.
  */

object MovingAverage {

  /** Main program method */
  def main(args: Array[String]) : Unit = {

    try {
      var properties: SetupArguments = new SetupArguments(args)
      System.out.println("Lets calculate the moving average")
      System.out.println(properties.toString())

      // get the execution environment
      val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
          env.getConfig.disableSysoutLogging
          env.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000))
          env.enableCheckpointing(5000) // create a checkpoint every 5 seconds

      val consumer = new FlinkKafkaConsumer010[String]( properties.getProperty("consumer"), new SimpleStringSchema(), properties )
      val stream = env.addSource(consumer)

      // write kafka stream to standard out.
      val results = stream
        .map { w => w.split("\\s").last; }
        .map(_.toString)
//        .keyBy(0)
//        .timeWindow(Time.seconds(5))
//        .fold()

      // print the results with a single thread, rather than in parallel
      results.print().setParallelism(1)
//      stream.print().setParallelism(1)  // raw output

      env.execute("Read from Kafka example")

    } catch {
    case e: Exception =>
      System.err.println("Problem with calculating moving average.")
      e.printStackTrace()
    }

  }
}

/*** Helper functions ***/

/**
  * Manage the arguments
  */
class SetupArguments() extends Properties {
  private var configresource = "/application.conf"
  private var a: Array[String] = _

  @throws[Exception]
  def this(args:Array[String]) {
    this()
    this.a = args
    val in = getClass().getResourceAsStream(this.configresource)
    this.load(in)
    in.close()
    val params = ParameterTool.fromArgs(this.a)
    // setup connection properties
    if (params.has("kafka_broker")) this.setProperty("bootstrap.servers", params.get("kafka_broker"))
    if (params.has("kafka_zookeeper")) this.setProperty("zookeeper.connect", params.get("kafka_zookeeper"))
    if (params.has("kafka_groupid")) this.setProperty("group.id", params.get("kafka_groupid"))
    if (params.has("kafka_producer_topic")) this.setProperty("producer", params.get("kafka_producer_topic"))
    if (params.has("kafka_consumer_topic")) this.setProperty("consumer", params.get("kafka_consumer_topic"))
  }

  @throws[Exception]
  def getParams(): ParameterTool = {
    return ParameterTool.fromArgs(this.a)
  }
}
