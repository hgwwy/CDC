package com.zsrd.debezium.common;

public class Constants {

    public static final String INSERT_SQL = "insert into %s values (%s) ";
    public static final String UPDATE_SQL = "update %s set %s where %s ";
    public static final String DELETE_SQL = "delete from %s where %s ";
}
