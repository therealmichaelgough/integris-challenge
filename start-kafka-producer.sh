#!/usr/bin/env bash

if [ "$1" == "-h" ]; then
  echo "Usage: `basename $0` start-kafka-producer.sh [topic_name] [counter_end] [sleep_time_millis] [s3_bucket_word_source]"
  return
fi

# get the ip of zookeeper
#BROKER_HOST=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' kafka_broker)
#BROKER_HOST=26d273e2f7fe
#BROKER_HOST=localhost
#BROKER_PORT=$(docker inspect --format '{{ (index (index .NetworkSettings.Ports "9092/tcp") 0).HostPort }}' kafka_broker)
#BROKER_PORT=9094
BROKER_HOST=localhost
BROKER_PORT=9092

# creds for a IAM user restricted to S3 reads on word source bucket
# assuming accessKeys.csv is in pwd
AWS_ACCESS_KEY_ID=$(cat ./accessKeys.csv | cut -d',' -f1)
AWS_SECRET_ACCESS_KEY=$(cat ./accessKeys.csv | cut -d',' -f2)

docker run -it --rm \
        -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
        -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
        -e BROKER_HOST=$BROKER_HOST \
        -e BROKER_PORT=$BROKER_PORT \
        -e TOPIC=$1 \
        -e COUNTER_END=$2 \
        -e SLEEP_TIME_IN_MILLIS=$3 \
        -e S3_BUCKET_NAME=$4 \
        --name kafka_producer \
        --network=integris-challenge_zookeeper_network \
        kafka_producer
