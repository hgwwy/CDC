package com.zsrd.debezium.kakfa.json.model;

import lombok.Data;

import java.util.List;

@Data
public class ExtSchema {

    /**
     * @return the type of this schema
     */
//    private org.apache.kafka.connect.data.ExtSchema.Type type;

    private String type;

    private boolean optional;

    private String name;

    private List<ExtField> fields;

}
