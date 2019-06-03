#!/usr/bin/env bash

ZK_HOST=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' zookeeper)
NIMBUS_HOST=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' storm_nimbus)

#ZK_PORT=$(docker port zookeeper | cut -d ":" -f 2)
ZK_PORT=2181
NIMBUS_THRIFT_PORT=$(docker port storm_nimbus | cut -d ":" -f 2)

BROKER_HOST=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' kafka_broker)
BROKER_PORT=29092

# usage example : ./submit-topology.sh com.michael.storm_word_counter.WordCountTopology word_counter_topology words

docker run -it --rm \
        -e MAINCLASS=$1 \
        -e TOPOLOGY_NAME=$2 \
        -e TOPIC=$3 \
        -e ZK_HOST=${ZK_HOST} \
        -e ZK_PORT=${ZK_PORT} \
        -e NIMBUS_HOST=${NIMBUS_HOST} \
        -e NIMBUS_THRIFT_PORT=${NIMBUS_THRIFT_PORT} \
        -e BROKER_HOST=${BROKER_HOST} \
        -e BROKER_PORT=${BROKER_PORT} \
        --name topology \
        --network=integris-challenge_zookeeper_network \
        storm_topology \
        "submit"
