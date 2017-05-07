FROM golang:1.6.2

RUN go get github.com/Shopify/sarama && \
    go get github.com/Sirupsen/logrus && \
    mkdir -p /workspace && \
    cd /workspace && \
    git clone https://github.com/dcos/demos && \
    cd /workspace/demos/1.9/fintrans/generator && \
    GOOS=linux go build -a -tags netgo -ldflags '-s -w' -o /usr/local/bin/generator && \
    rm -rf /go/pkg && \
    rm -rf /workspace/demos && \
    rm -rf /go/src

WORKDIR /workspace

ENTRYPOINT [ "/usr/local/bin/generator" ]
