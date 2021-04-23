package com.zsrd.debezium.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Jdbcutil {

    // 创建数据库的基本信息
    // 创建url
    private static String url = "jdbc:mysql://localhost:3306/cdc?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true";
    // 数据库的用户名和密码
    private static String user = "root";
    private static String password = "123456";
    public static Connection conn = null;

    public static Connection getConnection(String driverName) {
        // 创建连接对象
        try {
            Class.forName(driverName);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            HikariDataSource dataSource = new HikariDataSource(config);
            conn = dataSource.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return conn;

    }

    public static void close(Connection conn, Statement stsm) {
        if (stsm != null) {
            try {
                stsm.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
