package com.dcos.demo.flink

import java.util.Properties

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.common.restartstrategy.RestartStrategies
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
      properties.load()
      System.out.println("Lets calculate the moving average")
      System.out.println(properties.toString())

      // get the execution environment
      val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

      env.getConfig.disableSysoutLogging
      env.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000))
      env.enableCheckpointing(5000) // create a checkpoint every 5 seconds

      env.getConfig.setGlobalJobParameters(properties.getParams()) // make parameters available in the web interface

      val messageStream = env.addSource(new Nothing(parameterTool.getRequired("topic"), new SimpleStringSchema, parameterTool.getProperties))

      // write kafka stream to standard out.
      messageStream.print

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
class SetupArguments(args:Array[String]) extends Properties {
  private var configresource = "/application.conf"
  private var a: Array[String] = args

  @throws[Exception]
  def load() {
    this.a = args
    val in = getClass().getResourceAsStream(this.configresource)
    this.load(in)
    in.close()
    val params = ParameterTool.fromArgs(this.a)
    // setup connection properties
    if (params.has("kafka_broker")) this.setProperty("bootstrap.servers", params.get("kafka_broker"))
    if (params.has("kafka_producer_topic")) this.setProperty("producer", params.get("kafka_producer_topic"))
  }

  @throws[Exception]
  def getParams(): ParameterTool = {
    return ParameterTool.fromArgs(this.a)
  }
}
