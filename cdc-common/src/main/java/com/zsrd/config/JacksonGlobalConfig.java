package com.zsrd.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.TimeZone;

@Configuration
public class JacksonGlobalConfig implements WebMvcConfigurer {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return jacksonObjectMapperBuilder -> {
            //若POJO对象的属性值为null，序列化时不进行显示，暂时注释掉，为空也显示
//            jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);

            /*禁用一些配置*/
//            jacksonObjectMapperBuilder.failOnUnknownProperties(false);

            // 使科学计数法完整返回给前端
            jacksonObjectMapperBuilder.featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

            // 返回前端时间戳，前端根据需要自己转换格式
//            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


            /* 时间模块 */
            //JavaTimeModule javaTimeModule = new JavaTimeModule();
            /* 序列化配置, 针对java8 时间 项目推荐返回前端时间戳，前端根据需要自己转换格式*/
            //javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());

            /* 注册模块 */
            //jacksonObjectMapperBuilder.modules(javaTimeModule, new Jdk8Module(), new ParameterNamesModule());

            // 属性命名策略
            jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

            // 时区
            jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone("GMT+8"));

            // 针对于Date类型，文本格式化，Date 时间格式(非 java8 时间)，也统一用时间戳
            jacksonObjectMapperBuilder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //-----------------------------------------------华丽的分割线---------------------------------------------------
            //若POJO对象的属性值为null，序列化时不进行显示
//            jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
//            //若POJO对象的属性值为""，序列化时不进行显示
//            jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
//            //DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES相当于配置，JSON串含有未知字段时，反序列化依旧可以成功
//            jacksonObjectMapperBuilder.failOnUnknownProperties(false);
//            //序列化时的命名策略——驼峰命名法
//            jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//            //针对于Date类型，文本格式化
//            jacksonObjectMapperBuilder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            //针对于JDK新时间类。序列化时带有T的问题，自定义格式化字符串
//            JavaTimeModule javaTimeModule = new JavaTimeModule();
//            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            jacksonObjectMapperBuilder.modules(javaTimeModule);
//

//            //默认关闭，将char[]数组序列化为String类型。若开启后序列化为JSON数组。
//            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS);
//
//            //默认关闭，在类上使用@JsonRootName(value="rootNode")注解时是否可以包裹Root元素。
//            // (https://blog.csdn.net/blueheart20/article/details/52212221)
            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRAP_ROOT_VALUE);
//            //默认开启：如果一个类没有public的方法或属性时，会导致序列化失败。关闭后，会得到一个空JSON串。
//            jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//
//            //默认关闭，即以文本(ISO-8601)作为Key，开启后，以时间戳作为Key
//            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
//
//            //默认禁用，禁用情况下，需考虑WRITE_ENUMS_USING_TO_STRING配置。启用后，ENUM序列化为数字
//            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
            //默认关闭，枚举类型序列化方式，默认情况下使用Enum.name()。开启后，使用Enum.toString()。注：需重写Enum的toString方法;
            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
//
//            //默认关闭，当集合Collection或数组一个元素时返回："list":["a"]。开启后，"list":"a"
//            //需要注意，和DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY 配套使用，要么都开启，要么都关闭。
            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

//
//            //默认关闭，即序列化Number类型及子类为{"amount1":1.1}。开启后，序列化为String类型，即{"amount1":"1.1"}
            jacksonObjectMapperBuilder.featuresToEnable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
//
//            /******
//             *  反序列化
//             */
//            //默认关闭，当JSON字段为""(EMPTY_STRING)时，解析为普通的POJO对象抛出异常。开启后，该POJO的属性值为null。
            jacksonObjectMapperBuilder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        };
    }

}
