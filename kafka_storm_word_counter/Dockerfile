FROM maven:latest

MAINTAINER mdgough12

RUN mkdir --parents /usr/src/kafka_consumer
WORKDIR /usr/src/kafka_consumer

# the POM tells maven how to build the .jar which contains our kafka producer which sends words to the kafka broker
ADD pom.xml /usr/src/kafka_consumer

# Get all dependencies
RUN mvn verify clean --fail-never

# Add java source
ADD . /usr/src/kafka_consumer

# install and produce .jar according to Maven lifecycle
RUN mvn verify

ADD ./start-consumer.sh /usr/src/kafka_producer/start-consumer.sh
ENTRYPOINT ["/usr/src/kafka_producer/start-consumer.sh"]