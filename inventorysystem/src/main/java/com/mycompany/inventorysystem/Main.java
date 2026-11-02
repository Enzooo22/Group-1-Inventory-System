package com.mycompany.inventorysystem;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main extends JFrame implements ActionListener {

    private JLabel lblTitle;
    private JButton btnSuppliers, btnReports, btnLogout;
    private JPanel sidebar;
    private ProductPanel productPanel;
    Main() {

        setTitle("Inventory Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

    // TEST DATABASE CONNECTION
    try (java.sql.Connection con =
             DatabaseConnection.getConnection()) {

        System.out.println("Database connected!");

    } catch (java.sql.SQLException ex) {

        JOptionPane.showMessageDialog(
            null,
            "Database connection failed: " + ex.getMessage()
        );
    }

 
        lblTitle = new JLabel("INVENTORY MANAGEMENT SYSTEM");
        lblTitle.setBounds(250, 15, 400, 30);
        add(lblTitle);

        sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBounds(10, 60, 180, 540);
        add(sidebar);

        btnSuppliers = new JButton("Suppliers");
        btnSuppliers.setBounds(10, 20, 160, 35);
        sidebar.add(btnSuppliers);

        btnReports = new JButton("Reports");
        btnReports.setBounds(10, 65, 160, 35);
        sidebar.add(btnReports);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(10, 485, 160, 35);
        sidebar.add(btnLogout);

        productPanel = new ProductPanel(this);
        productPanel.setBounds(200, 60, 770, 540);
        add(productPanel);

        btnSuppliers.addActionListener(this);
        btnReports.addActionListener(this);
        btnLogout.addActionListener(this);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSuppliers) {
            new SupplierManagement();
        }
        if (e.getSource() == btnReports) {
            new Reports();
        }
        if (e.getSource() == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

}
