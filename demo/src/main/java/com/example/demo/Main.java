package com.example.demo;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DataRetriever Data = new DataRetriever();
        Order o = Data.findOrderByReference("ORD00201");
        System.out.println("Order Reference: " + o);
        }
    }