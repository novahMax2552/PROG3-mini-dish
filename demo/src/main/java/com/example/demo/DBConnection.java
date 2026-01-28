package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getDBConnection() throws SQLException {

        String url = "jdbc:postgresql://localhost:5432/mini_dish_db";
        String user = "mini_dish_db_manager";
        String password = "....";

        return DriverManager.getConnection(url, user, password);
    }
}
