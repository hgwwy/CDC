package com.zsrd.debezium.kakfa.json.parser;


import com.zsrd.debezium.kakfa.json.model.ExtField;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class MicroTimeParser implements kafkaConsumerParser<Object, LocalTime> {


    @Override
    public LocalTime parse(ExtField field, Object value) {
        return LocalTime.ofNanoOfDay((long) value * TimeUnit.MICROSECONDS.toNanos(1));
    }
}
