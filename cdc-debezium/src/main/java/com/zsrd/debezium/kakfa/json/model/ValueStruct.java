package com.zsrd.debezium.kakfa.json.model;

import lombok.Data;

import java.util.Map;

@Data
public class ValueStruct {

    private ExtSchema schema;

    private Payload payload;

    @Data
    public static class Payload {

        private Map<String, Object> before;

        private Map<String, Object> after;

        private Source source;

        private String op;

        private Long ts_ms;
    }

    @Data
    public static class Source {

        private String version;
        private String connector;
        private String name;
        private int server_id;
        private int ts_sec;
        private Object gtid;
        private String file;
        private int pos;
        private int row;
        private boolean snapshot;
        private int thread;
        private String db;
        private String table;
        private String query;
    }

}
