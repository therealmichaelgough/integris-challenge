#!/usr/bin/env bash

case "$1" in
        initial)
            docker build -t=kafka_producer kafka_producer
            docker build -t=storm_topology kafka_storm_word_counter

            docker-compose build
            ;;

        kafka_producer)
            docker build -t=kafka_producer kafka_producer
            ;;

        storm_topology)
            docker build -t=storm_topology kafka_storm_word_counter
            ;;

        *)
            echo $"Usage: $0 {initial|kafka_producer|storm_topology}"
            exit 1
esac