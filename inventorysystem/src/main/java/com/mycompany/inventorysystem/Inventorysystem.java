

package com.mycompany.inventorysystem;


public class Inventorysystem {
    public static void main(String[] args) {
        
        DatabaseConnection.CreateDatabase();
        DatabaseConnection.CreateTable();
        
        Login app = new Login();
        app.setVisible(true);
    }
}