package com.zsrd.debezium.kakfa.avro;

import com.zsrd.debezium.common.DriverConstants;
import com.zsrd.debezium.utils.Jdbcutil;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainTest {

    // 创建数据库的基本信息
    // 创建url
    private static String url = "jdbc:oracle:thin:@172.16.17.104:1521:xe";
    // 数据库的用户名和密码
    private static String user = "CDC";
    private static String pwd = "123456";

    public static void main1(String[] args) throws ParseException {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        Date result = sdf.parse(str);
        System.out.println(str);
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = Jdbcutil.getConnection(DriverConstants.ORACLE, url, user, pwd);
        Assert.notNull(connection, "获取连接失败");
        Statement statement = connection.createStatement();
        statement.execute("insert into test values(1,'haha',{ts '2021-04-25 22:00:31'})");
        Jdbcutil.close(connection, statement);
    }
}
