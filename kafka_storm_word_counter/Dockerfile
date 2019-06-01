FROM maven:latest

MAINTAINER mdgough12


RUN apt-get update && \
    apt-get install -y unzip wget


RUN wget -q -O - https://archive.apache.org/dist/storm/apache-storm-1.2.2/apache-storm-1.2.2.tar.gz  | tar -xzf - -C /opt

ENV STORM_HOME /opt/apache-storm-1.2.2

RUN groupadd storm; useradd --gid storm --home-dir /home/storm --create-home --shell /bin/bash storm; chown -R storm:storm $STORM_HOME; mkdir /var/log/storm ; chown -R storm:storm /var/log/storm
RUN ln -s $STORM_HOME/bin/storm /usr/bin/storm

RUN mkdir --parents /usr/src/kafka_storm_word_counter
WORKDIR /usr/src/kafka_storm_word_counter

# the POM tells maven how to build the .jar which contains our kafka producer which sends words to the kafka broker
ADD pom.xml /usr/src/kafka_storm_word_counter

# Get all dependencies
RUN mvn verify clean --fail-never

# Add java source
ADD . /usr/src/kafka_storm_word_counter

# install and produce .jar according to Maven lifecycle
RUN mvn verify

ADD ./start-consumer.sh /usr/src/kafka_storm_word_counter/start-consumer.sh

ENTRYPOINT ["/usr/src/kafka_storm_word_counter/start-consumer.sh"]