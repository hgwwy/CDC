package com.zsrd.debezium.kakfa.avro;

import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

public class MainTest {
    public static void main(String[] args) {
        String str = "update user set name= ?,sex= ?,update_time= ? where id= ? ";
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(str);
        Object[] params = {"张三",1.1,"2011-05-10 12:20:58",4};
        /*for (int i = 0;i< params.length;i++) {
            Object o = params[i];
            if (o instanceof String) {
                str = str.replaceFirst("\\?", "'"+o+"'");
            } else {
                str = str.replaceFirst("\\?",String.valueOf(o));
            }
        }*/
        System.out.println(str);
    }
}
