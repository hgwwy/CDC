package com.zsrd.debezium.kakfa.json.model;

import lombok.Data;

import java.util.Map;

@Data
public class KeyStruct {

    private ExtSchema schema;

    private Map<String, Object> payload;
}
