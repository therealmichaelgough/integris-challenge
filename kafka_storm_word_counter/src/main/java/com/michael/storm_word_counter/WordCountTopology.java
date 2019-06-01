package com.michael.storm_word_counter;

//import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.*;
import org.apache.storm.Config;
import org.apache.storm.tuple.*;


import com.michael.storm_word_counter.bolt.CounterBolt;
import com.michael.storm_word_counter.bolt.RankerBolt;
import com.michael.storm_word_counter.bolt.SplitterBolt;

import org.apache.storm.StormSubmitter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;


import java.util.Arrays;

public class WordCountTopology {

    private static final Logger LOG = LoggerFactory.getLogger(WordCountTopology.class);

    private static final String KAFKA_SPOUT_ID = "kafka-spout";
    private static final String SPLITTER_BOLT_ID = "splitter-bolt";
    private static final String COUNTER_BOLT_ID = "counter-bolt";
    private static final String RANKER_BOLT_ID = "ranker-bolt";

    public WordCountTopology(String ZK_HOST, String ZK_PORT) {

    }

    public WordCountTopology() {

    }

    /**
     * WordCountTopology with Kafka
     *
     * @return      StormTopology Object
     */
    public StormTopology buildTopology(String TOPIC) {
        //brokerHosts = new ZkHosts(ZK_HOST + ":" + ZK_PORT);
        /*KafkaSpoutConfig spoutConf = KafkaSpoutConfig.builder("localhost:9092", topic)
                .setGroupId(consumerGroupId)
                .setOffsetCommitPeriodMs(10_000)
                .setFirstPollOffsetStrategy(UNCOMMITTED_LATEST)
                .setMaxUncommittedOffsets(1000000)
                .setRetry(kafkaSpoutRetryService)
                .setRecordTranslator
                        (new TupleBuilder(), outputFields, topic )
                .build();
        kafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme());
*/
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(KAFKA_SPOUT_ID, new org.apache.storm.kafka.spout.KafkaSpout<>(org.apache.storm.kafka.spout.KafkaSpoutConfig.builder("localhost:9092", "words").build()), 1);
        builder.setBolt(SPLITTER_BOLT_ID, new SplitterBolt(), 4).shuffleGrouping(KAFKA_SPOUT_ID);
        builder.setBolt(COUNTER_BOLT_ID, new CounterBolt(), 4).fieldsGrouping(SPLITTER_BOLT_ID, new Fields("word"));
        builder.setBolt(RANKER_BOLT_ID, new RankerBolt()).globalGrouping(COUNTER_BOLT_ID);

        return builder.createTopology();
    }

    public static void main(String[] args) throws Exception {

        Config conf = new Config();
        String TOPOLOGY_NAME;


        TOPOLOGY_NAME = args[0];
        /**
         * Remote deployment as part of Docker Compose multi-application setup
         *
         * @TOPOLOGY_NAME:       Name of Storm topology
         * @ZK_HOST:             Host IP address of ZooKeeper
         * @ZK_PORT:             Port of ZooKeeper
         * @TOPIC:               Kafka Topic which this Storm topology is consuming from
         */
        LOG.info("Submitting topology " + TOPOLOGY_NAME + " to remote cluster.");
        String ZK_HOST = args[1];
        int ZK_PORT = Integer.parseInt(args[2]);
        String TOPIC = args[3];
        String NIMBUS_HOST = args[4];
        int NIMBUS_THRIFT_PORT = Integer.parseInt(args[5]);

        conf.setDebug(false);
        conf.setNumWorkers(2);
        conf.setMaxTaskParallelism(5);
        conf.put(Config.NIMBUS_HOST, NIMBUS_HOST);
        conf.put(Config.NIMBUS_THRIFT_PORT, NIMBUS_THRIFT_PORT);
        conf.put(Config.STORM_ZOOKEEPER_PORT, ZK_PORT);
        conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ZK_HOST));

        WordCountTopology wordCountTopology = new WordCountTopology(ZK_HOST, String.valueOf(ZK_PORT));
        StormSubmitter.submitTopology(TOPOLOGY_NAME, conf, wordCountTopology.buildTopology(TOPIC));



    }
}