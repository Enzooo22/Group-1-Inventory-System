
package com.mycompany.inventorysystem;


import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class Login extends JFrame implements ActionListener {

    private JLabel lblTitle, lblUsername, lblPassword;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnClear;

    public Login() {

        setTitle("Inventory Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        lblTitle = new JLabel("INVENTORY SYSTEM LOGIN");
        lblTitle.setBounds(80, 25, 250, 30);
        add(lblTitle);

        lblUsername = new JLabel("Username:");
        lblUsername.setBounds(40, 80, 100, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(140, 80, 200, 25);
        add(txtUsername);

        lblPassword = new JLabel("Password:");
        lblPassword.setBounds(40, 120, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 120, 200, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(80, 180, 100, 30);
        add(btnLogin);

        btnClear = new JButton("Clear");
        btnClear.setBounds(200, 180, 100, 30);
        add(btnClear);

        btnLogin.addActionListener(this);
        btnClear.addActionListener(this);

        getRootPane().setDefaultButton(btnLogin);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String hashPassword(String password)
            throws Exception {

        MessageDigest md =
            MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(
            password.getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder hex = new StringBuilder();

        for (byte b : hash) {
            hex.append(String.format("%02x", b & 0xff));
        }

        return hex.toString();
    }

    private boolean checkLogin(
            String username, String password) {

        String sql = "SELECT user_id FROM users " +
            "WHERE username = ? AND password_hash = ? " +
            "AND role = 'ADMIN'";

        try (Connection con =
                 DatabaseConnection.getConnection();
             PreparedStatement ps =
                 con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashPassword(password));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Login error: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnLogin) {

            String username =
                txtUsername.getText().trim();

            String password =
                new String(txtPassword.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password."
                );
                return;
            }

            if (checkLogin(username, password)) {

                JOptionPane.showMessageDialog(
                    this, "Login successful!"
                );

                dispose();

                new Main();

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password."
                );

                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        }

        if (e.getSource() == btnClear) {
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocus();
        }
    }
}
    

