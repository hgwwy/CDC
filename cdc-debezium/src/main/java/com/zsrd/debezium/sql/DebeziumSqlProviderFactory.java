package com.zsrd.debezium.sql;

import com.google.common.collect.Maps;
import io.debezium.data.Envelope;

import java.util.Map;

public class DebeziumSqlProviderFactory {

    private static Map<Envelope.Operation, AbstractDebeziumSqlProvider> map = Maps.newHashMap();

    static {
        map.put(Envelope.Operation.CREATE, new DebeziumInsertSqlProvider());
        map.put(Envelope.Operation.UPDATE, new DebeziumUpdateSqlProvider());
        map.put(Envelope.Operation.DELETE, new DebeziumDeleteSqlProvider());
    }

    public static AbstractDebeziumSqlProvider getProvider(Envelope.Operation operation) {
        return map.get(operation);
    }


}
