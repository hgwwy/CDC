package com.zsrd.debezium.kakfa.avro.parser;

import com.google.common.collect.Maps;

import java.util.Map;

public class KafkaConsumerAvroParserFactory {

    private static Map<String, kafkaParser> parserMap = Maps.newHashMap();

    static {
        parserMap.put(io.debezium.time.ZonedTimestamp.SCHEMA_NAME, new ZonedTimestampParser());
        parserMap.put(io.debezium.time.Timestamp.SCHEMA_NAME, new TimestampParser());
        parserMap.put(io.debezium.time.Date.SCHEMA_NAME, new DateParser());
        parserMap.put(io.debezium.time.MicroTime.SCHEMA_NAME, new MicroTimeParser());
        parserMap.put(io.debezium.data.geometry.Geometry.LOGICAL_NAME, new GeometryParser());
        parserMap.put(io.debezium.data.geometry.Point.LOGICAL_NAME, new PointParser());
    }

    public static kafkaParser getParser(String schemaName) {
        return parserMap.get(schemaName);
    }
}
