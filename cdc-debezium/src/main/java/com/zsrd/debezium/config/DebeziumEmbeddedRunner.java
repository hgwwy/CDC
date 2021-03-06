package com.zsrd.debezium.config;

import com.zsrd.debezium.sql.AbstractDebeziumSqlProvider;
import com.zsrd.debezium.sql.DebeziumSqlProviderFactory;
import com.zsrd.debezium.utils.DebeziumRecordUtils;
import io.debezium.connector.mysql.MySqlConnectorConfig;
import io.debezium.data.Envelope;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.util.Clock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
//@Order(2)
//@Component
public class DebeziumEmbeddedRunner implements CommandLineRunner {

    @Autowired
    private io.debezium.config.Configuration embeddedConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedTemplate;

    @Autowired
    private JsonConverter keyConverter;

    @Autowired
    private JsonConverter valueConverter;

    @Override
    public void run(String... args) throws Exception {
        EmbeddedEngine engine = EmbeddedEngine.create()
                .using(embeddedConfig)
                .using(this.getClass().getClassLoader())
                .using(Clock.SYSTEM)
                .notifying(this::handleRecord)
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(engine);

        shutdownHook(engine);

        awaitTermination(executor);
    }


    private void handleRecord(SourceRecord record) {
        logRecord(record);

        Struct payload = (Struct) record.value();
        if (Objects.isNull(payload)) {
            return;
        }
        String table = Optional.ofNullable(DebeziumRecordUtils.getRecordStructValue(payload, "source"))
                .map(s -> s.getString("table")).orElse(null);

//        // ????????????DML
        Envelope.Operation operation = DebeziumRecordUtils.getOperation(payload);
        if (Objects.nonNull(operation)) {
            Struct key = (Struct) record.key();
            handleDML(key, payload, table, operation);
            return;
        }
//
//        // ????????????DDL
        String ddl = getDDL(payload);
        if (StringUtils.isNotBlank(ddl)) {
            handleDDL(ddl);
        }
    }

    private String getDDL(Struct payload) {
        String ddl = DebeziumRecordUtils.getDDL(payload);
        if (StringUtils.isBlank(ddl)) {
            return null;
        }
        String db = DebeziumRecordUtils.getDatabaseName(payload);
        if (StringUtils.isBlank(db)) {
            db = embeddedConfig.getString(MySqlConnectorConfig.DATABASE_WHITELIST);
        }
        ddl = ddl.replace(db + ".", "");
        ddl = ddl.replace("`" + db + "`.", "");
        return ddl;
    }

    /**
     * ???????????????ddl??????
     *
     * @param ddl
     */
    private void handleDDL(String ddl) {
        log.info("ddl?????? : {}", ddl);
        try {
            jdbcTemplate.execute(ddl);
        } catch (Exception e) {
            log.error("???????????????DDL???????????????", e);
        }
    }

    /**
     * ??????insert,update,delete???DML??????
     *
     * @param key       ???????????????????????????
     * @param payload   ???????????????
     * @param table     ??????
     * @param operation DML????????????
     */
    private void handleDML(Struct key, Struct payload, String table, Envelope.Operation operation) {
        AbstractDebeziumSqlProvider provider = DebeziumSqlProviderFactory.getProvider(operation);
        if (Objects.isNull(provider)) {
            log.error("????????????sql??????????????????.");
            return;
        }

        String sql = provider.getSql(key, payload, table);
        if (StringUtils.isBlank(sql)) {
            log.error("?????????sql.");
            return;
        }

        try {
            log.info("dml?????? : {}", sql);
            namedTemplate.update(sql, provider.getSqlParameterMap());
        } catch (Exception e) {
            log.error("?????????DML???????????????", e);
        }
    }

    /**
     * ????????????
     *
     * @param record
     */
    private void logRecord(SourceRecord record) {
        final byte[] payload = valueConverter.fromConnectData("dummy", record.valueSchema(), record.value());
        final byte[] key = keyConverter.fromConnectData("dummy", record.keySchema(), record.key());
        log.info("Publishing Topic --> {}", record.topic());
        log.info("Key --> {}", new String(key));
        log.info("Payload --> {}", new String(payload));
    }

    private void shutdownHook(EmbeddedEngine engine) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Requesting embedded engine to shut down");
            engine.stop();
        }));
    }

    private void awaitTermination(ExecutorService executor) {
        try {
            while (!executor.awaitTermination(10L, TimeUnit.SECONDS)) {
                log.info("Waiting another 10 seconds for the embedded engine to shut down");
            }
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }

}