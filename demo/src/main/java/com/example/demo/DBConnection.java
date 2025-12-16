package com.example.demo;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DBConnection {
    public final String JADBC_URL = "jdbc:postgresql://localhost:5432/mini_dish_db";
    public final String JDBC_USER = "mini_dish_manager";
    public final String JDBC_PASSWORD = "....";

    public Connection getDBConnection() throws Exception {
        return java.sql.DriverManager.getConnection(JADBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
