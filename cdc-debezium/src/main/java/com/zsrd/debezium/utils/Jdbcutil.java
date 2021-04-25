package com.zsrd.debezium.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Jdbcutil {
    public static Connection conn = null;

    public static Connection getConnection(String driverName, String url, String user, String pwd) {
        // 创建连接对象
        try {
            Class.forName(driverName);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(pwd);
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
