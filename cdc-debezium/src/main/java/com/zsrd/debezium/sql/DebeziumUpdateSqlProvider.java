package com.zsrd.debezium.sql;

import com.zsrd.debezium.common.DMLEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.data.Field;

import java.util.function.Function;
import java.util.function.Predicate;

public class DebeziumUpdateSqlProvider extends AbstractDebeziumSqlProvider {

    @Override
    protected Predicate<Field> getFieldPredicate() {
        return field -> !primaryKeyList.contains(field.name());
    }

    @Override
    protected Function<String, String> getColumnNameFunction() {
        return columnName -> columnName + "= :" + columnName;
    }

    @Override
    protected boolean needParseColumn() {
        return true;
    }

    @Override
    protected boolean needParsePrimaryKey() {
        return true;
    }

    @Override
    protected String generateSql(String table) {
        return DMLEnum.UPDATE_SQL.generateSQL(table, StringUtils.join(preparedColumnList, ","),
                StringUtils.join(preparedPrimaryKeyList, " and "));
    }
}
