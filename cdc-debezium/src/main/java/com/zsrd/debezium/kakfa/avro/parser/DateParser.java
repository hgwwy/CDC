package com.zsrd.debezium.kakfa.avro.parser;


import org.apache.avro.Schema;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateParser implements kafkaParser<Object, String> {

    @Override
    public String parse(Schema schema, Object value) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();
        return "{d '" + LocalDate.ofEpochDay((long)(int)value).format(formatter) + "'}";
    }
}
