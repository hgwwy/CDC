package com.zsrd.debezium.kakfa.avro.sql;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zsrd.debezium.kakfa.avro.parser.KafkaConsumerAvroParserFactory;
import com.zsrd.debezium.kakfa.avro.parser.kafkaParser;
import com.zsrd.utils.CharUtils;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class SqlProvider {

    /**
     * 主键值作为参数的map
     */
    private Map<String, Object> sqlParameterMap = null;

    /**
     * 表列预编译表达式集合
     */
    protected List<String> preparedColumnList = null;

    /**
     * 主键预编译表达式集合
     */
    protected List<String> preparedPrimaryKeyList = null;

    /**
     * 主键集合
     */
    protected List<String> primaryKeyList = null;

    public String getSql(GenericData.Record key, GenericData.Record value, String table) {
        sqlParameterMap = Maps.newHashMap();

        // 解析表主键
        if (needParsePrimaryKey()) {
            parsePrimaryKey(key);
        }

        if (needParseColumn()) {
            // 获取数据库cdc事件之后的值
            GenericData.Record after = (GenericData.Record) value.get("after");

            // 处理表的列
            handleColumn(after, getFieldPredicate(), getColumnNameFunction());
        }

        return generateSql(table);

    }

    protected Predicate<Schema.Field> getFieldPredicate() {
        return field -> true;
    }

    protected Function<String, String> getColumnNameFunction() {
        return columnName -> columnName;
    }

    protected abstract boolean needParseColumn();

    protected abstract boolean needParsePrimaryKey();

    protected abstract String generateSql(String table);

    /**
     * 解析表主键
     *
     * @param key 记录主键结果
     */
    protected void parsePrimaryKey(GenericData.Record key) {
        primaryKeyList = Lists.newArrayList();
        preparedPrimaryKeyList = Lists.newArrayList();

        key.getSchema().getFields().stream().forEach(field -> {
            String primaryKey = field.name();
            preparedPrimaryKeyList.add(primaryKey + "= :" + primaryKey);
            primaryKeyList.add(primaryKey);
            Object primaryKeyValue = key.get(primaryKey);
            sqlParameterMap.put(primaryKey, parseColumnValue(field, primaryKeyValue));
        });
    }

    /**
     * 处理表列
     *
     * @param predicate 是否需要过滤
     * @param function  列表名操作函数
     */
    protected void handleColumn(GenericData.Record after, Predicate<Schema.Field> predicate,
                                Function<String, String> function) {
        preparedColumnList = Lists.newArrayList();

        after.getSchema().getFields().stream()
                .filter(predicate)
                .forEach(field -> {
                    String columnName = field.name();
                    Object columnValue = after.get(columnName);
                    preparedColumnList.add(function.apply(columnName));

                    sqlParameterMap.put(columnName, parseColumnValue(field, columnValue));
                });
    }

    /**
     * 解析表列的值
     *
     * @param field
     * @param value
     */
    private Object parseColumnValue(Schema.Field field, Object value) {
        if (Objects.isNull(value)) {
            return null;
        }

        if (value instanceof ByteBuffer) {
            return CharUtils.getByte((ByteBuffer) value);
        }

        Schema feildSchema = field.schema();
        Schema.Type type = feildSchema.getType();
        if (type == Schema.Type.STRING) {
            return value.toString();
        } else if (type == Schema.Type.INT || type == Schema.Type.LONG) {
            return value;
        } else if (type == Schema.Type.UNION) {
            Schema schema = feildSchema.getTypes().stream().filter(s -> s.getType() != Schema.Type.NULL)
                    .findFirst().orElse(null);

            String connectName = schema.getProp("connect.name");
            kafkaParser parser = KafkaConsumerAvroParserFactory.getParser(connectName);
            if (Objects.nonNull(parser)) {
                return parser.parse(schema, value);
            }
        }

        return value;
    }

    public Map<String, Object> getSqlParameterMap() {
        return sqlParameterMap;
    }
}