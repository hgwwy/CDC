package com.zsrd.debezium.kakfa.json.parser;

import com.google.common.collect.Maps;

import java.util.Map;

public class KakfaConsumerParserFactory {

    private static Map<String, kafkaConsumerParser> parserMap = Maps.newHashMap();

    static {
        parserMap.put(io.debezium.time.ZonedTimestamp.SCHEMA_NAME, new ZonedTimestampParser());
        parserMap.put(io.debezium.time.Timestamp.SCHEMA_NAME, new TimestampParser());
        parserMap.put(io.debezium.time.Date.SCHEMA_NAME, new DateParser());
        parserMap.put(io.debezium.time.MicroTime.SCHEMA_NAME, new MicroTimeParser());
        parserMap.put(io.debezium.data.geometry.Geometry.LOGICAL_NAME, new GeometryParser());
        parserMap.put(io.debezium.data.geometry.Point.LOGICAL_NAME, new PointParser());
    }

    public static kafkaConsumerParser getParser(String schemaName) {
        return parserMap.get(schemaName);
    }
}
