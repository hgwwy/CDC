## 本项目主要是2种方式演示debezium捕捉 同步数据

### 第一种：内嵌式Embedding形式监听事件，并同步更新到数据库
    1.这种方式不需要使用kafka，直接消费debezium事件流
    2.本次使用的是监听 inventory数据库并将数据同步到 jpa数据源配置的cdc数据库中
    3.依赖配置文件 config.properties
    4.主要配置类 DebeziumEmbeddedAutoConfiguration.Class  DebeziumEmbeddedRunner.Class

### 第二种：Debezium MySQL CDC Connector同步更新到数据库
    0.本例需要 zookeeper  kafka  mysql  kafka-connect 等服务的支持
    1.需要kafka的支持，监听 消费kafka数据达到同步功能
    2.每张表都有不同的schema，所以需要一个schema-registry来统一管理
    3.
    4.
    5.创建kafka connect
        curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors
        {
            "name": "inventory-connector",
            "config": {
            "connector.class": "io.debezium.connector.mysql.MySqlConnector",
            "tasks.max": "1",
            "database.hostname": "mysql",
            "database.port": "3306",
            "database.user": "root",
            "database.password": "123456",
            "database.server.id": "184054",
            "database.server.name": "dbserver1",
            "database.serverTimezone":"UTC" ,
            "database.whitelist": "inventory",
            "decimal.handling.mode": "double",
            "key.converter": "io.confluent.connect.avro.AvroConverter",
            "key.converter.schema.registry.url": "http://172.17.0.6:8081",
            "value.converter": "io.confluent.connect.avro.AvroConverter",
            "value.converter.schema.registry.url": "http://172.17.0.6:8081",
            "database.history.kafka.bootstrap.servers": "localhost:9092",
            "database.history.kafka.topic": "dbhistory.inventory"
            }
        }
    6.查询connect  http://localhost:8083/connectors/inventory-connector
    7.本例使用的数据传输格式Avro,已在application.yml配置解析类
    8.核心处理类 KafkaAvroConsumerRunner.Class
    9.资料参考 https://debezium.io/documentation/reference/1.4/configuration/avro.html