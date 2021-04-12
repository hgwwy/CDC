package com.zsrd.debezium.parser;

import org.apache.kafka.connect.data.Schema;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class MicroTimeParser implements DebeziumParser<Object, LocalTime> {


    @Override
    public LocalTime parse(Schema schema, Object value) {
        return LocalTime.ofNanoOfDay((long) value * TimeUnit.MICROSECONDS.toNanos(1));
    }
}
