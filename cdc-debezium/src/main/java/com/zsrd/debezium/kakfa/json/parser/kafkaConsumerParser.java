package com.zsrd.debezium.kakfa.json.parser;

import com.zsrd.debezium.kakfa.json.model.ExtField;

@FunctionalInterface
public interface kafkaConsumerParser<T, R> {

    R parse(ExtField field, T t);

}
