package com.leonardo.kafkademo.faker;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class DataGenerator {

    private Faker faker;

    public DataGenerator() {
        this.faker = new Faker();
    }

    public Record generateRecord() {
        Record r = new Record();
        r.setTs(""+new Timestamp(System.currentTimeMillis()).getTime());
        r.setDeviceId(UUID.randomUUID().toString());
        r.setLat(faker.address().latitude());
        r.setLon(faker.address().longitude());

        return r;
    }

}
