package com.michael.storm_word_counter.spout;

import org.apache.storm.kafka.spout.KafkaSpoutConfig.Builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * DataSourceSpout
 */
public class WordConsumerSpout extends BaseRichSpout {

    private static final Logger LOG = LoggerFactory.getLogger(WordConsumerSpout.class);
    private SpoutOutputCollector collector;

    KafkaSpoutConfig spoutConf =  KafkaSpoutConfig.builder(bootStrapServers, topic)
            .setGroupId(consumerGroupId)
            .setOffsetCommitPeriodMs(10_000)
            .setFirstPollOffsetStrategy(UNCOMMITTED_LATEST)
            .setMaxUncommittedOffsets(1000000)
            .setRetry(kafkaSpoutRetryService)
            .setRecordTranslator
                    (new TupleBuilder(), outputFields, topic )
            .build();

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {

        this.collector = spoutOutputCollector;
        this.rand = new Random();
    }

    public void nextTuple() {

        String sentence = sentences[(int)(Math.random()*(sentences.length-1))];
        collector.emit(new Values(sentence));

        Utils.waitForMillis(500);
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare (new Fields("sentence"));
    }
}