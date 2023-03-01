package com.leonardo.kafkademo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Properties;

@SpringBootApplication
public class KafkademoApplication {

	@Value("${kafka.bootstrapServers}")
	private String bootstrapServers;

	public static void main(String[] args) {
		SpringApplication.run(KafkademoApplication.class, args);
	}

	@Bean
	public Properties kafkaProps() {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", bootstrapServers);
		properties.setProperty("key.serializer", StringSerializer.class.getName());
		properties.setProperty("value.serializer", StringSerializer.class.getName());
		properties.setProperty("key.deserializer", StringDeserializer.class.getName());
		properties.setProperty("value.deserializer", StringDeserializer.class.getName());

		return properties;
	}

	@Bean
	public HashMap<String, KafkaProducer> kafkaProducers() {
		HashMap<String, KafkaProducer> kafkaProducers = new HashMap<>();

		return kafkaProducers;
	}

	@Bean
	public HashMap<String, KafkaProducer> kafkaConsumers() {
		HashMap<String, KafkaProducer> kafkaConsumers = new HashMap<>();

		return kafkaConsumers;
	}
}
