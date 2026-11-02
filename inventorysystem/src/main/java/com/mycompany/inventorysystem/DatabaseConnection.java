/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.inventorysystem;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement; 

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/inventory_management";

    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void CreateDatabase() {
        String sql = "CREATE DATABASE IF NOT EXISTS inventory_management;";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/",
                USER,
                PASS);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

            System.out.println("Database is ready!");

        } catch (SQLException e) {
            System.out.println("Failed to create database!");
            e.printStackTrace();
        }
    }
    
   public static void CreateTable() {
    String sql = "CREATE TABLE IF NOT EXISTS users ("
            + "user_id INT AUTO_INCREMENT PRIMARY KEY,"
            + "username VARCHAR(100) NOT NULL UNIQUE,"
            + "password_hash VARCHAR(255) NOT NULL,"
            + "role ENUM ('ADMIN', 'USER') NOT NULL DEFAULT 'USER'"
            + ")";

    try (Connection C = getConnection();
         Statement st = C.createStatement()) {

        st.executeUpdate(sql);
        System.out.println("Table has been created successfully!");

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    }
}
