package com.zsrd.debezium.kakfa.avro.parser;

import org.apache.avro.Schema;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class TimestampParser implements kafkaParser<Object, String> {

    @Override
    public String parse(Schema schema, Object value) {
        return "{ts '" + DateFormatUtils.format(new Date((long) value - 8 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm:ss")+ "'}";
    }
}
