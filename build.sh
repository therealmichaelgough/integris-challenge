#!/usr/bin/env bash

case "$1" in
        initial)
            docker build -t=kafka_producer kafka_producer
            docker build -t=storm_topology storm

            docker-compose build
            ;;

        kafka_producer)
            docker build -t=kafka_producer kafka_producer
            ;;

        storm_topology)
            docker build -t=storm_topology storm
            ;;

        *)
            echo $"Usage: $0 {initial|kafka-producer|storm-topology}"
            exit 1
esac