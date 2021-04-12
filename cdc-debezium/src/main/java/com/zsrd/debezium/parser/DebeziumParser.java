package com.zsrd.debezium.parser;

import org.apache.kafka.connect.data.Schema;

@FunctionalInterface
public interface DebeziumParser<T, R> {

    R parse(Schema schema, T t);

}
