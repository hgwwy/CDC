package com.zsrd.debezium.jdbc;

import com.zsrd.debezium.common.DriverConstants;
import com.zsrd.debezium.utils.Jdbcutil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DmlParser {

    /**
     * jdbc执行DML语句
     */
    public static void testDML() {
        Statement statement = null;
        Connection con = null;
        try {
            con = Jdbcutil.getConnection(DriverConstants.MYSQL_LOW);
            statement = con.createStatement();
            String dml_sql = "insert into test(name) values('name6')";
            int num = statement.executeUpdate(dml_sql);
            System.out.println("受影响的行数为：" + num);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            Jdbcutil.close(con, statement);
        }

    }

}
