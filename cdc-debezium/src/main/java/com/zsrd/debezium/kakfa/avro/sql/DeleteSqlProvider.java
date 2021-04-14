package com.zsrd.debezium.kakfa.avro.sql;

import com.zsrd.debezium.common.DMLEnum;
import org.apache.commons.lang3.StringUtils;

public class DeleteSqlProvider extends SqlProvider {

    @Override
    protected boolean needParseColumn() {
        return false;
    }

    @Override
    protected boolean needParsePrimaryKey() {
        return true;
    }

    @Override
    protected String generateSql(String table) {
        return DMLEnum.DELETE_SQL.generateSQL(table, StringUtils.join(preparedPrimaryKeyList, " and "));
    }
}
