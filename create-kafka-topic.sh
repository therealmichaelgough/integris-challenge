#!/usr/bin/env bash

if [ "$1" == "-h" ]; then
  echo "Usage: `basename $0` create-kafka-topic.sh [replication_factor] [partitions] [topic_name]"
  return
fi

# get the ip of zookeeper
ZOOKEEPER_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' zookeeper)
#BROKER_PORT=$(docker inspect --format '{{ (index (index .NetworkSettings.Ports "9092/tcp") 0).HostPort }}' kafka_broker)
ZOOKEEPER_PORT=2181

docker exec -it kafka_broker /bin/bash -c "/usr/bin/kafka-topics --create  --replication-factor $1 --partitions $2 --zookeeper $ZOOKEEPER_IP:$ZOOKEEPER_PORT --topic $3"
