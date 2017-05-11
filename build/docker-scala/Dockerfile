FROM debian:jessie

RUN \
    echo "===> add webupd8 repository..."  && \
    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list  && \
    echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list  && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886  && \
    apt-get update  && \
    \
    \
    echo "===> install Java"  && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections  && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections  && \
    DEBIAN_FRONTEND=noninteractive \
         apt-get install -y --force-yes \
         oracle-java7-installer \
         oracle-java7-set-default \
         curl bash snappy \
         maven  && \
    \
    \
    echo "===> clean up..."  && \
    rm -rf /var/cache/oracle-jdk7-installer  && \
    apt-get clean  && \
    rm -rf /var/lib/apt/lists/*

ENV SCALA_VERSION 2.11.6
ENV SCALA_TARBALL http://www.scala-lang.org/files/archive/scala-$SCALA_VERSION.deb
RUN echo "==> Install curl helper tool..."  && \
    apt-get update  && \
    DEBIAN_FRONTEND=noninteractive  apt-get install -y --force-yes curl  && \
    \
    \
    \
    echo "===> install from Typesafe repo (contains old versions but they have all dependencies we need later on)"  && \
    curl -sSL http://apt.typesafe.com/repo-deb-build-0002.deb  -o repo-deb.deb  && \
    dpkg -i repo-deb.deb  && \
    apt-get update        && \
    \
    \
    \
    echo "===> install Scala"  && \
    DEBIAN_FRONTEND=noninteractive \
    apt-get install -y --force-yes libjansi-java  && \
    curl -sSL $SCALA_TARBALL -o scala.deb             && \
    dpkg -i scala.deb                                 && \
    \
    \
    \
    echo "===> clean up..."  && \
    rm -f *.deb                            && \
    apt-get remove -y --auto-remove curl   && \
    apt-get clean                          && \
    rm -rf /var/lib/apt/lists/*

