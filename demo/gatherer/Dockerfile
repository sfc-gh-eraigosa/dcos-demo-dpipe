# Switch to mesosphere container since official image is 1.2.1 vs 1.2.0
#FROM flink:1.2
FROM mesosphere/dcos-flink:1.2.0-1.4

ENV PATH=$PATH:$FLINK_HOME/bin
ENV FLINK_MASTER=192.168.0.4:19285
ENV FLINK_CLASS=com.dcos.demo.flink.SocketWindowWordCount
ENV FLINK_JAR=/workspace/gatherer.jar

ENV KEEP_UP=false
ENV KEEP_UP_TIMEOUT=0

ADD ./entrypoint.sh /workspace/entrypoint.sh
ADD ./target/gatherer-1.0-SNAPSHOT-jar-with-dependencies.jar /workspace/gatherer.jar
RUN chmod +x /workspace/entrypoint.sh

WORKDIR /workspace

ENTRYPOINT [ "/workspace/entrypoint.sh" ]
CMD ["--help"]
