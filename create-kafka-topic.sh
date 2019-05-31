#!/usr/bin/env bash

if [ "$1" == "-h" ]; then
  echo "Usage: `basename $0` create-kafka-topic.sh [replication_factor] [partitions] [topic_name]"
  return
fi

# get the ip of zookeeper
BROKER_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' kafka_broker)
#BROKER_PORT=$(docker inspect --format '{{ (index (index .NetworkSettings.Ports "9092/tcp") 0).HostPort }}' kafka_broker)

docker exec -it kafka_broker /bin/bash -c "/opt/bitnami/kafka/bin/kafka-topics.sh --create --bootstrap-server $BROKER_IP:9092 --replication-factor $1 --partitions $2 --topic $3"