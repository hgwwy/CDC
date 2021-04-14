package com.zsrd.debezium.kakfa.json.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExtField {

    private String type;

    private boolean optional;

    private String name;

    private int version;

    private Map<String, String> parameters;

    private String field;

    @JsonProperty("default")
    private String defaultValue;

    private List<ExtField> fields;

}
