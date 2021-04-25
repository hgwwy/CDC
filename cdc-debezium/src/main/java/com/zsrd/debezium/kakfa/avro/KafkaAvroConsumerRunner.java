package com.zsrd.debezium.kakfa.avro;

import com.zsrd.debezium.kakfa.avro.sql.SqlProvider;
import com.zsrd.debezium.kakfa.avro.sql.SqlProviderFactory;
import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.apache.avro.util.Utf8;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;


@Slf4j
@Component
public class KafkaAvroConsumerRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @KafkaListener(id = "dbserver1-ddl-consumer", topics = "dbserver1")
    public void listenerUser(ConsumerRecord<GenericData.Record, GenericData.Record> record) throws Exception {
        GenericData.Record key = record.key();
        GenericData.Record value = record.value();
        log.info("Received record: {}", record);
        log.info("Received record: key {}", key);
        log.info("Received record: value {}", value);

        String databaseName = Optional.ofNullable(value.get("databaseName")).map(Object::toString).orElse(null);
        String ddl = Optional.ofNullable(value.get("ddl")).map(Object::toString).orElse(null);

        if (StringUtils.isBlank(ddl)) {
            return;
        }
        handleDDL(ddl, databaseName);
    }

    /**
     * 执行数据库ddl语句
     *
     * @param ddl
     */
    private void handleDDL(String ddl, String db) {
        log.info("ddl语句 : {}", ddl);
        try {
            if (StringUtils.isNotBlank(db)) {
                ddl = ddl.replace(db + ".", "");
                ddl = ddl.replace("`" + db + "`.", "");
            }

            jdbcTemplate.execute(ddl);
        } catch (Exception e) {
            log.error("数据库操作DDL语句失败，", e);
        }
    }

    @KafkaListener(id = "dbserver1-dml-consumer", topicPattern = "dbserver1.inventory.*")
    public void listenerAvro(ConsumerRecord<GenericData.Record, GenericData.Record> record) throws Exception {
        GenericData.Record key = record.key();
        GenericData.Record value = record.value();
        log.info("Received record: {}", record);
        log.info("Received record: key {}", key);
        log.info("Received record: value {}", value);

        if (Objects.isNull(value)) {
            return;
        }

        GenericData.Record source = (GenericData.Record) value.get("source");
        String table = source.get("table").toString();
        Envelope.Operation operation = Envelope.Operation.forCode(value.get("op").toString());

        String db = source.get("db").toString();

        handleDML(key, value, table, operation);
    }

    private void handleDML(GenericData.Record key, GenericData.Record value,
                           String table, Envelope.Operation operation) {
        try {
            String driverName = jdbcTemplate.getDataSource().getConnection().getMetaData().getDriverName();
            if (driverName.equals("Oracle JDBC driver")) {
                table = "\""+ StringUtils.upperCase(table) + "\"";
            }
            SqlProvider provider = SqlProviderFactory.getProvider(operation);
            if (Objects.isNull(provider)) {
                log.error("没有找到sql处理器提供者.");
                return;
            }

            String sql = provider.getSql(key, value, table);
            if (StringUtils.isBlank(sql)) {
                log.error("找不到sql.");
                return;
            }

            MapSqlParameterSource parameterSource = new MapSqlParameterSource(provider.getSqlParameterMap());
            String result_sql = NamedParameterUtils.substituteNamedParameters(sql, parameterSource);
            ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
            Object[] params = NamedParameterUtils.buildValueArray(parsedSql, parameterSource, null);
            for (int i = 0; i < params.length; i++) {
                Object o = params[i];
                if (o instanceof Utf8) {
                    result_sql = result_sql.replaceFirst("\\?", "'" + o + "'");
                }else {
                    result_sql = result_sql.replaceFirst("\\?", String.valueOf(o));
                }
            }
            log.info("dml语句 : {}", result_sql);
            jdbcTemplate.execute(result_sql);
        } catch (Exception e) {
            log.error("数据库DML操作失败，", e);
        }
    }

}
