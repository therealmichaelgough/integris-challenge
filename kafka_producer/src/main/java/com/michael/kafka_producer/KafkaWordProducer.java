package com.michael.kafka_producer;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.AWSCredentials;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.regions.Regions;


import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.io.ByteArrayOutputStream;

/**
 * KafkaWordProducer
 * feeds strings to a Kafka Broker
 */
public class KafkaWordProducer {

    static boolean runThread;
    static int COUNTER_START;
    static int COUNTER_END;
    static int SLEEP_TIME_IN_MILLIS;
    static String BROKER_HOST;
    static String BROKER_PORT;
    static String TOPIC;
    static String PARTITION_KEY;
    static ProducerConfig conf;
    static Producer<String, String> producer;

    static String S3_BUCKET_NAME;

    //public AmazonS3 S3Client;

    public static String readS3ObjectAsString(AmazonS3 s3Client, String bucketName, String objectKey) throws IOException {
        int total_read = 0;
        try (S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucketName, objectKey)))
        {
            Long filesize = fullObject.getObjectMetadata().getContentLength();
            System.out.println(String.format("Downloading %s/%s (%d)", bucketName, objectKey, filesize));
            S3ObjectInputStream inputStream = fullObject.getObjectContent();

            ByteArrayOutputStream completeFile = new ByteArrayOutputStream();
            byte[] readBuffer = new byte[4096];
            int destPos = 0;
            int read_len = 0;

            while ((read_len = inputStream.read(readBuffer)) > 0) {
                total_read = total_read + read_len;
                System.out.println("bytes read: " + total_read);
                completeFile.write(readBuffer); //, destPos, readBuffer.length);
                destPos = destPos + readBuffer.length;
            }
            return completeFile.toString(Charset.forName("utf-8"));

        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw e;
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            throw e;
        }

    }

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
        String theString = writer.toString();
        System.out.println(theString);
        return theString;
    }

    public static void main(String [] args) {

        /**
         * @BROKER_HOST:            IP of Docker Host (mainly VM) where dockerized Broker instance is running.
         * @BROKER_PORT:            Mapped port of dockerized Broker instance on Host (VM).
         * @TOPIC:                  Published topic to which a consumer (e.g. Apache Storm Kafka Spout) subscribes
         *                          in order to receive messages.
         * @COUNTER_END:            Count of published messages.
         * @SLEEP_TIME_IN_MILLIS:   Amount of time in between to messages.
         * @S3_BUCKET_NAME:         Name of S3 bucket from which to read input strings.
         */


        BROKER_HOST = args[0];
        BROKER_PORT = args[1];
        TOPIC = args[2];
        COUNTER_END = Integer.parseInt(args[3]);
        SLEEP_TIME_IN_MILLIS = Integer.parseInt(args[4]);
        S3_BUCKET_NAME = args[5];

        COUNTER_START = 0;
        runThread = true;
        //PARTITION_KEY = "wordcount";

        Properties props = new Properties();
        props.put("bootstrap.servers", BROKER_HOST + ":" + BROKER_PORT);
        //props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "1");

        //conf = new ProducerConfig(props);
        producer = new KafkaProducer<>(props);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .build();

        ObjectListing objectListing = s3Client.listObjects(S3_BUCKET_NAME);

        Thread thread = new Thread() {
            public void run() {
                while (runThread){
                    if(COUNTER_START < COUNTER_END) {
                        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                            try {
                                String s3ObjectContent = readS3ObjectAsString(s3Client, S3_BUCKET_NAME, objectSummary.getKey());
                                System.out.println(s3ObjectContent);
                                //String s3objectContentString = inputStreamToString(s3ObjectContent);
                                /*TODO:
                                   * parse json from individual objects
                                *  * potentially limit reads to a given number of articles rather than S3 objects
                                * */
                                ProducerRecord<String, String> message = new ProducerRecord<String, String>(TOPIC, s3ObjectContent);
                                producer.send(message);
                                COUNTER_START++;
                            } catch (IOException e){

                                continue;
                            }

                            try {
                                Thread.sleep(SLEEP_TIME_IN_MILLIS);
                            } catch (InterruptedException e) {

                            }
                        }
                    }
                    else {
                        runThread = false;
                    }
                }
                producer.close();
            }
        };
        thread.start();
    } // end main()
} // end
