/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.inventorysystem;

public class Inventorysystem {
    public static void main(String[] args) {
        
        DatabaseConnection.CreateDatabase();
        
        Login app = new Login();
        app.setVisible(true);
    }
}