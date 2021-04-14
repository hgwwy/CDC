package com.zsrd.debezium.kakfa.avro.parser;


import org.apache.avro.Schema;
import java.time.LocalDate;

public class DateParser implements kafkaParser<Object, LocalDate> {

    @Override
    public LocalDate parse(Schema schema, Object value) {
        return LocalDate.ofEpochDay((long) (int) value + 1);
    }
}
