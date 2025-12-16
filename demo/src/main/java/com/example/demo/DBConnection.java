package com.example.demo;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DBConnection {
    public final String URL = "jdbc:postgresql://localhost:5432/mini_dish_db";
    public final String USER = "mini_dish_manager";
    public final String PASSWORD = "....";

    public Connection getDBConnection() throws Exception {
        return java.sql.DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
