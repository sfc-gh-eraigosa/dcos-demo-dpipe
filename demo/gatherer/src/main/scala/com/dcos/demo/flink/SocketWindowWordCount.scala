package com.dcos.demo.flink

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer09
import java.io.{File, FileInputStream}

import org.apache.flink.streaming.util.serialization.SimpleStringSchema
import java.util.Properties


/**
  * Implements a streaming windowed version of the "WordCount" program.
  *
  * This program connects to a server socket and reads strings from the socket.
  * The easiest way to try this out is to open a text sever (at port 12345)
  * using the <i>netcat</i> tool via
  * <pre>
  * nc -l 12345
  * </pre>
  * and run this example with the hostname and the port as arguments..
  */
object SocketWindowWordCount {

  /** Main program method */
  def main(args: Array[String]) : Unit = {

    // the host and the port to connect to
    var hostname: String = "localhost"
    var port: Int = 0

    // create and load default properties// create and load default properties
    // Load the defaults

    var properties: Properties = new Properties()
    val in = new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "application.conf").getAbsolutePath())
    properties.load(in)
    in.close()
    val params = ParameterTool.fromArgs(args)

    try {

      hostname = if (params.has("hostname")) params.get("hostname") else "localhost"
      port = params.getInt("port")
      // setup connection properties
      if (params.has("kafka_broker")) properties.setProperty("bootstrap.servers", params.get("kafka_broker"))
      if (params.has("kafka_producer_topic")) properties.setProperty("producer", params.get("kafka_producer_topic"))
    } catch {
      case e: Exception =>
        System.err.println("No port specified. Please run 'SocketWindowWordCount " +
          "--hostname <hostname> --port <port>', where hostname (localhost by default) and port " +
          "is the address of the text server")
        System.err.println("To start a simple text server, run 'netcat -l -p <port> $(hostname)' " +
          "and type the input text into the command line")
        return

    }

    // get the execution environment
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    try {
      // get input data by connecting to the socket
      val text = env.socketTextStream(hostname, port, '\n')

      // parse the data, group it, window it, and aggregate the counts
      val windowCounts = text
        .flatMap { w => w.split("\\s") }
        .map { w => WordWithCount(w, 1) }
        .keyBy("word")
        .timeWindow(Time.seconds(5))
        .sum("count")
        .map(_.toString)

      // print the results with a single thread, rather than in parallel
      //    windowCounts.print().setParallelism(1)
      val producer = new FlinkKafkaProducer09[String](
        "wordcountresults",
        new SimpleStringSchema(),
        properties
      )
      producer.setLogFailuresOnly(false)
      producer.setFlushOnCheckpoint(true)
      windowCounts.addSink(producer)
      env.execute("Socket Window WordCount")
    } catch {
      case e: Exception =>
        System.err.printf("To start a simple text server, run 'netcat -l -p %s $(hostname)' " +
          "and type the input text into the command line\n",port.toString())
        e.printStackTrace()
    }
  }
  /** Data type for words with count */
  case class WordWithCount(word: String, count: Long)

}
