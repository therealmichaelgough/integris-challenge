#!/bin/bash

# env variables are provided by ../start-kafka-producer.sh
# path of this script is hard-coded in ^

java -jar target/kafka_producer-1.0-SNAPSHOT-jar-with-dependencies.jar $BROKER_HOST $BROKER_PORT $TOPIC $COUNTER_END $SLEEP_TIME_IN_MILLIS $S3_BUCKET_NAME