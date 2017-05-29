package com.dcos.demo.flink

import java.util.Properties

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.{SlidingProcessingTimeWindows, TumblingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaProducer09}
import org.apache.flink.streaming.util.serialization.SimpleStringSchema

/**
  * Created by wenlock on 5/23/17.
  * Lets use the movingaverage stream to determine volatility on a given label
  * with a provided confidence interval.  when the confidence interval is greater than the
  * coieficient of variance then volatility is High.  When it's less than the coieficient of
  * variance the volatility is Low.  Provide the output on a new kafka message stream.
  *
  * The window of measurement is 1 minute tumbling window, and we compare against the mean
  * of the coieficient of variance.
  */

object Volatility {

  /** Main program method */
  def main(args: Array[String]) : Unit = {

    try {
      var properties: SetupArguments = new SetupArguments(args)
      System.out.println("Lets calculate the volatility")
      System.out.println(properties.toString())
      System.out.printf("Consumer ==> %s\n",properties.getProperty("volatility.consumer"))
      System.out.printf("Producer ==> %s\n",properties.getProperty("volatility.producer"))
      val label: String = properties.getProperty("volatility.label").toString;
      val confindex: Long = properties.getProperty("volatility.confindex").toLong;

      // get the execution environment
      val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
          env.getConfig.disableSysoutLogging
          env.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000))
          env.enableCheckpointing(5000) // create a checkpoint every 5 seconds seconds

      val consumer = new FlinkKafkaConsumer010[String]( properties.getProperty("volatility.consumer"), new SimpleStringSchema(), properties )
      val stream = env.addSource(consumer)

      val results = stream
        .map{ s =>
                val a: Array[String] = s.split("\\s");
                val l: String = a(1);
                val m: Long = a(4).toLong;
                val cofv: Float = a(6).toFloat;
                val vrecord: VolatilityRecord = new VolatilityRecord( l, 1, confindex, m, cofv )
                vrecord;
             }
             .filter( s => s.label == label ) // only apply the window to label
             .keyBy("label")
             .window(TumblingProcessingTimeWindows.of(Time.seconds(20)))
             .reduce((t1, t2) => {
                      var samples: Long = t1.sample + t2.sample;
                      var summean: Long = t1.mean + t2.mean;
                      var sumcofv: Float = t1.cofv + t2.cofv;
                      new VolatilityRecord(t1.label, samples, confindex, summean, sumcofv)
              })


      // print the results with a single thread, rather than in parallel
      results.print().setParallelism(1)

      // add a producer to place the moving average in
      val producer = new FlinkKafkaProducer09[String](
        properties.getProperty("volatility.producer"),
        new SimpleStringSchema(),
        properties
      )
      producer.setLogFailuresOnly(false)
      producer.setFlushOnCheckpoint(true)
      var kafka_results = results.map(_.toString())
      kafka_results.addSink(producer)

      env.execute("Run Volatility Analysis")

    } catch {
    case e: Exception =>
      System.err.println("Problem with calculating volatility.")
      e.printStackTrace()
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
      if (params.has("producer_topic")) this.setProperty("volatility.producer", params.get("producer_topic"))
      if (params.has("consumer_topic")) this.setProperty("volatility.consumer", params.get("consumer_topic"))
      if (params.has("label")) this.setProperty("volatility.label", params.get("label"))
      if (params.has("conf_index")) this.setProperty("volatility.confindex", params.get("conf_index"))
    }

    @throws[Exception]
    def getParams(): ParameterTool = {
      return ParameterTool.fromArgs(this.a)
    }
  }

}
