package com.zsrd.debezium.kakfa.json.parser;

import com.zsrd.debezium.kakfa.json.model.ExtField;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class TimestampParser implements kafkaConsumerParser<Object, String> {

    @Override
    public String parse(ExtField field, Object value) {
        return DateFormatUtils.format(new Date((long) value), "yyyy-MM-dd HH:mm:ss");
    }
}
