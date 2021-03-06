package com.zsrd.debezium.kakfa.json;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zsrd.debezium.common.DMLEnum;
import com.zsrd.debezium.kakfa.json.model.ExtField;
import com.zsrd.debezium.kakfa.json.model.KeyStruct;
import com.zsrd.debezium.kakfa.json.model.ValueStruct;
import com.zsrd.debezium.kakfa.json.parser.KakfaConsumerParserFactory;
import com.zsrd.debezium.kakfa.json.parser.kafkaConsumerParser;
import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Slf4j
//@Component
public class KafkaJsonConsumerRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedTemplate;

    @Autowired
    private ObjectMapper debeObjectMapper;

    @KafkaListener(topics = "dbserver1.inventory_back.user")
    public void listenerUser(ConsumerRecord record) throws Exception {
        log.info("Received record: {}", record);
        log.info("Received record: key {}", record.key());
        log.info("Received record: value {}", record.value());
    }

    @KafkaListener(id = "d1", topics = {"dbserver1.inventory.demo"})
    public void listener(ConsumerRecord<String, String> record) throws Exception {
        KeyStruct key = debeObjectMapper.readValue(record.key(), KeyStruct.class);
        ValueStruct value = debeObjectMapper.readValue(record.value(), ValueStruct.class);
        log.info("Received record: key {}", key);
        log.info("Received record: value {}", value);

        String table = Optional.ofNullable(value).map(v -> v.getPayload()).map(p -> p.getSource()).map(s -> s.getTable()).orElse(null);
        String op = Optional.ofNullable(value).map(v -> v.getPayload()).map(p -> p.getOp()).orElse(null);
        Envelope.Operation operation = Envelope.Operation.forCode(op);

        log.info("table is {}, op is {}, operation is {}", table, op, operation);
        Map<String, Object> sqlParameterMap = Maps.newHashMap();
        List<String> preparedColumnList = Lists.newArrayList();
        List<String> preparedPrimaryKeyList = Lists.newArrayList();
        List<String> primaryKeyList = Lists.newArrayList();
        key.getSchema().getFields().stream().forEach(field -> {
            String primaryKey = field.getField();
            preparedPrimaryKeyList.add(primaryKey + "= :" + primaryKey);
            primaryKeyList.add(primaryKey);
            Object primaryKeyValue = key.getPayload().get(primaryKey);
            sqlParameterMap.put(primaryKey, parseColumnValue(field, primaryKeyValue));
        });

        // ???????????????cdc??????????????????
        Map<String, Object> after = Optional.ofNullable(value.getPayload()).map(p -> p.getAfter()).orElse(null);
        List<ExtField> fieldList = Optional.ofNullable(value.getSchema()).map(p -> p.getFields()).orElse(null);
        ExtField afterField = fieldList.stream().filter(f -> Objects.equals(f.getField(), "after"))
                .findFirst().orElse(null);


        // ???????????????
        afterField.getFields().stream()
                .filter(field -> !primaryKeyList.contains(field.getField()))
                .forEach(field -> {
                    String columnName = field.getField();
                    preparedColumnList.add(columnName + "= :" + columnName);
                    Object columnValue = value.getPayload().getAfter().get(columnName);
                    sqlParameterMap.put(columnName, parseColumnValue(field, columnValue));
                });

        log.info("sqlParameterMap {}", sqlParameterMap);


        String sql = DMLEnum.UPDATE_SQL.generateSQL(table, StringUtils.join(preparedColumnList, ","),
                StringUtils.join(preparedPrimaryKeyList, " and "));

        try {
            log.info("dml?????? : {}", sql);
            namedTemplate.update(sql, sqlParameterMap);
        } catch (Exception e) {
            log.error("?????????DML???????????????", e);
        }


    }

    protected Object parseColumnValue(ExtField field, Object value) {
        if (Objects.isNull(value)) {
            return null;
        }

        if (Objects.equals(field.getType(),"bytes")) {

            return value.toString().getBytes();
        }

        String schemaName = field.getName();
        kafkaConsumerParser parser = KakfaConsumerParserFactory.getParser(schemaName);
        if (Objects.nonNull(parser)) {
            return parser.parse(field, value);
        }
        return value;
    }

}
