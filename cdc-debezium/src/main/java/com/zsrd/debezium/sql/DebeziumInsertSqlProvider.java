package com.zsrd.debezium.sql;

import com.zsrd.debezium.common.DMLEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class DebeziumInsertSqlProvider extends AbstractDebeziumSqlProvider {

    @Override
    protected boolean needParseColumn() {
        return true;
    }

    @Override
    protected boolean needParsePrimaryKey() {
        return false;
    }

    @Override
    protected Function<String, String> getColumnNameFunction() {
        return columnName -> ":" + columnName;
    }

    @Override
    protected String generateSql(String table) {
        return DMLEnum.INSERT_SQL.generateSQL(table, StringUtils.join(preparedColumnList, ","));
    }
}
