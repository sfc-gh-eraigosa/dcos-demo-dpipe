package com.dcos.demo.flink

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer09


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
    var LOCAL_KAFKA_BROKER = "localhost:9092"
    var KAFKA_PRODUCER_TOPIC = "wordcountResults"

    // the host and the port to connect to
    var hostname: String = "localhost"
    var port: Int = 0

    try {
      val params = ParameterTool.fromArgs(args)
      hostname = if (params.has("hostname")) params.get("hostname") else "localhost"
      port = params.getInt("port")
    } catch {
      case e: Exception =>
        System.err.println("No port specified. Please run 'SocketWindowWordCount " +
          "--hostname <hostname> --port <port>', where hostname (localhost by default) and port " +
          "is the address of the text server")
        System.err.println("To start a simple text server, run 'netcat -l <port>' " +
          "and type the input text into the command line")
        return

    }

    // get the execution environment
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // get input data by connecting to the socket
    val text = env.socketTextStream(hostname, port, '\n')

    // parse the data, group it, window it, and aggregate the counts
    val windowCounts = text
      .flatMap { w => w.split("\\s") }
      .map { w => WordWithCount(w, 1) }
      .keyBy("word")
      .timeWindow(Time.seconds(5))
      .sum("count")

    // print the results with a single thread, rather than in parallel
//    windowCounts.print().setParallelism(1)

      windowCounts.addSink(
        new FlinkKafkaProducer09[WordWithCount](
          LOCAL_KAFKA_BROKER,
          KAFKA_PRODUCER_TOPIC,
          new WordWithCount
        )
      )
    env.execute("Socket Window WordCount")
  }

  /** Data type for words with count */
  case class WordWithCount(word: String, count: Long)
}