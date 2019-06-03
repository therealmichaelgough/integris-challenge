# Docker-S3-Kafka-Storm
## A demonstration project for Integris

### prerequisites
1. Docker installed on the host machine
2. An S3 bucket containing plain text objects
2. AWS access keys for an IAM role which reads from the S3 bucket, stored in accessKeys.csv

### Running the pipeline
1. docker-compose up -d
2. ./build initial
3. ./create-kafka-topic.sh [replication_factor] [partitions] [topic_name]
4. ./submit-topology.sh com.michael.storm_word_counter.WordCountTopology [topology name] [kafka topic]
5. ./start-kafka-producer.sh [kafka topic] [producer counter end] [producer sleep time millis] [s3 bucket name] (e.g. ./start-kafka-producer.sh words 1000 100 mgough-wikipedia)

### Status
This project is a WIP. Using the current configuration, there are no apparent connection errors on either the kafka producer or consumer, but the storm topology is not receiving messages from Kafka.
