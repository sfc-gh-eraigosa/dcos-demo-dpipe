FROM flink

ENV FLINK_MASTER=192.168.0.4:19285
ENV FLINK_CLASS=com.dcos.demo.flink.SocketWindowWordCount
ENV FLINK_JAR=/workspace/gatherer.jar


ADD ./entrypoint.sh /workspace/entrypoint.sh
ADD ./target/gatherer-1.0-SNAPSHOT-jar-with-dependencies.jar /workspace/gatherer.jar
RUN chmod +x /workspace/entrypoint.sh

WORKDIR /workspace

ENTRYPOINT [ "/workspace/entrypoint.sh" ]
CMD ["--help"]
