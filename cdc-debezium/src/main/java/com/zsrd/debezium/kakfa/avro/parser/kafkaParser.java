package com.zsrd.debezium.kakfa.avro.parser;

import org.apache.avro.Schema;

@FunctionalInterface
public interface kafkaParser<T, R> {

    R parse(Schema schema, T t);

}
