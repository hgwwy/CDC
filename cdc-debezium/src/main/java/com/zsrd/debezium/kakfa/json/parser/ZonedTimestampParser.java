package com.zsrd.debezium.kakfa.json.parser;

import com.zsrd.debezium.kakfa.json.model.ExtField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.util.Date;

@Slf4j
public class ZonedTimestampParser implements kafkaConsumerParser<Object, String> {

    @Override
    public String parse(ExtField field, Object input) {
        try {
            Date date = DateFormatUtils.ISO_DATETIME_FORMAT.parse(input.toString());
            return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            log.error("解析ZonedTimestamp失败，", e);
        }
        return null;
    }
}
