package com.zsrd.debezium.kakfa.avro.parser;

import org.apache.avro.Schema;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class MicroTimeParser implements kafkaParser<Object, LocalTime> {


    @Override
    public LocalTime parse(Schema schema, Object value) {
        return LocalTime.ofNanoOfDay((long) value * TimeUnit.MICROSECONDS.toNanos(1));
    }
}
