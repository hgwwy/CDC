package com.zsrd.debezium.kakfa.json.parser;


import com.zsrd.debezium.kakfa.json.model.ExtField;

import java.time.LocalDate;

public class DateParser implements kafkaConsumerParser<Object, LocalDate> {

    @Override
    public LocalDate parse(ExtField field, Object value) {
        return LocalDate.ofEpochDay((long) (int) value + 1);
    }
}
