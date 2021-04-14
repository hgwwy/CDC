package com.zsrd.debezium.config;

import org.apache.kafka.connect.json.JsonConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//@Configuration
public class DebeziumEmbeddedAutoConfiguration {

    @Bean
    public Properties embeddedProperties() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            Resource resource = new InputStreamResource(inputStream);
            return PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public io.debezium.config.Configuration embeddedConfig(Properties embeddedProperties) {
        return io.debezium.config.Configuration.from(embeddedProperties);
    }

    @Bean
    public JsonConverter keyConverter(io.debezium.config.Configuration embeddedConfig) {
        JsonConverter converter = new JsonConverter();
        converter.configure(embeddedConfig.asMap(), true);
        return converter;
    }

    @Bean
    public JsonConverter valueConverter(io.debezium.config.Configuration embeddedConfig) {
        JsonConverter converter = new JsonConverter();
        converter.configure(embeddedConfig.asMap(), false);
        return converter;
    }

}