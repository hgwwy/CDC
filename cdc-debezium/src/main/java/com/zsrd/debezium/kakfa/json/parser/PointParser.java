package com.zsrd.debezium.kakfa.json.parser;


import com.zsrd.debezium.kakfa.json.model.ExtField;
import com.zsrd.utils.CharUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PointParser implements kafkaConsumerParser<Map<String, Object>, byte[]> {

    @Override
    public byte[] parse(ExtField fieldSchema, Map<String, Object> value) {
        List<byte[]> geo = fieldSchema.getFields().stream()
                .map(field -> {
                    Object o = value.get(field.getField());
                    if (o == null) {
                        return CharUtils.intToBytes(0);
                    } else if (o instanceof Double) {
                        return null;
                    }
                    return o.toString().getBytes();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(array -> array.length))
                .collect(Collectors.toList());
        return CharUtils.byteMergerAll(geo);
    }
}
