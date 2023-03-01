package com.leonardo.kafkademo.api;

import com.leonardo.kafkademo.faker.DataGenerator;
import com.leonardo.kafkademo.faker.Record;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.*;

@RestController
@RequestMapping("api/kafka")
public class Controller {

    @Autowired
    private Properties kafkaProps;
    @Autowired
    private HashMap<String, KafkaProducer> kafkaProducers;
    @Autowired
    private HashMap<String, KafkaProducer> kafkaConsumers;
    @Autowired
    private DataGenerator dataFaker;
    @Value("${test.topic}")
    private String testTopic;

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @GetMapping("producer/new")
    public String newProducer() {
        String id = UUID.randomUUID().toString();
        startProducer(id);
        return id;
    }

    @GetMapping(path = "consumer/new", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> startConsumer(@RequestParam(required = false) String id) {
        String groupId = (id==null || "".equals(id)) ? UUID.randomUUID().toString() : id;

        Properties consumerProps = (Properties) kafkaProps.clone();
        consumerProps.setProperty("group.id", groupId);

        ReceiverOptions<String, String> options = ReceiverOptions.create(consumerProps);
        options = options.subscription(Collections.singletonList(testTopic));

        Flux<String> inboundFlux = KafkaReceiver.create(options)
                .receive()
                .map(r -> "topic: "+r.topic()+", partition: "+r.partition()+" value: "+r.value());

        return inboundFlux;
    }

    @GetMapping("producer/stop")
    public String stopProducer(@RequestParam String id) {
        String message = "OK";
        if (kafkaProducers.containsKey(id)) {
            logger.info("Stopping producer: "+id+"...");
            KafkaProducer producer = kafkaProducers.get(id);
            producer.flush();
            producer.close();
            kafkaProducers.remove(id);
        } else {
            logger.info("Producer '"+id+"' NOT FOUND!");
            message = "NOT FOUND!";
        }
        return message;
    }

    @GetMapping("producer/list")
    public Set<String> listProducers() {
        return kafkaProducers.keySet();
    }

    public boolean startProducer(String id) {
        try {
        logger.info("Starting producer: "+id+"...");
        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);
        kafkaProducers.put(id, producer);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean stop=false;
                while (!stop) {
                    Record rec = dataFaker.generateRecord();
                    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(testTopic, rec.toString());
                    if (kafkaProducers.containsKey(id))
                        producer.send(producerRecord);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return true;
    }

}
