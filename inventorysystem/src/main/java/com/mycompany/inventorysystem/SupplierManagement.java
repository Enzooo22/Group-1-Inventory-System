
package com.mycompany.inventorysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class SupplierManagement extends JFrame
        implements ActionListener {

    private JLabel lblTitle;
    private JTextField txtSearch;

    private JTable tblSuppliers;
    private DefaultTableModel model;

    private JButton btnAdd, btnUpdate;
    private JButton btnDelete, btnSearch, btnBack;

    SupplierManagement() {

        setTitle("Supplier Management");
        setSize(950, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        lblTitle = new JLabel("SUPPLIER MANAGEMENT");
        lblTitle.setBounds(350, 20, 300, 30);
        add(lblTitle);

        txtSearch = new JTextField();
        txtSearch.setBounds(30, 80, 250, 30);
        add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(290, 80, 100, 30);
        add(btnSearch);

        btnAdd = new JButton("Add Supplier");
        btnAdd.setBounds(30, 130, 130, 30);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(170, 130, 100, 30);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(280, 130, 100, 30);
        add(btnDelete);

        btnBack = new JButton("Back");
        btnBack.setBounds(800, 130, 100, 30);
        add(btnBack);

        String[] columns = {
            "Supplier ID",
            "Supplier Name",
            "Contact Person",
            "Phone",
            "Email",
            "Address"
        };

        model = new DefaultTableModel(columns, 0);
        tblSuppliers = new JTable(model);

        JScrollPane scrollPane =
        new JScrollPane(tblSuppliers);

        scrollPane.setBounds(30, 190, 870, 330);
        add(scrollPane);

        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnSearch.addActionListener(this);
        btnBack.addActionListener(this);

        loadSuppliers();

        setLocationRelativeTo(null);
        setVisible(true);
    }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == btnAdd) {

                String id = JOptionPane.showInputDialog(
                    this, "Enter Supplier ID:"
                );

                if (id == null || id.trim().isEmpty()) {
                    return;
                }

                String name = JOptionPane.showInputDialog(
                    this, "Enter Supplier Name:"
                );

                if (name == null || name.trim().isEmpty()) {
                    return;
                }

                String person = JOptionPane.showInputDialog(
                    this, "Enter Contact Person:"
                );

                if (person == null || person.trim().isEmpty()) {
                    return;
                }

                String phone = JOptionPane.showInputDialog(
                    this, "Enter Phone Number:"
                );

                if (phone == null || phone.trim().isEmpty()) {
                    return;
                }

                String email = JOptionPane.showInputDialog(
                    this, "Enter Email:"
                );

                if (email == null || email.trim().isEmpty()) {
                    return;
                }

                String address = JOptionPane.showInputDialog(
                    this, "Enter Address:"
                );

                if (address == null || address.trim().isEmpty()) {
                    return;
                }

                try {
                    Long.parseLong(id.trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        this, "Supplier ID must be a whole number."
                    );
                    return;
                }

                boolean saved = saveSupplier(
                    id.trim(),
                    name.trim(),
                    person.trim(),
                    phone.trim(),
                    email.trim(),
                    address.trim()
                );

                if (saved) {

                    loadSuppliers();

                    JOptionPane.showMessageDialog(
                        this, "Supplier added successfully!"
                    );
                }
            }

            if (e.getSource() == btnUpdate) {

                int selectedRow =
                    tblSuppliers.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(
                        this, "Please select a supplier to update."
                    );
                    return;
                }

                String id = model.getValueAt(
                    selectedRow, 0
                ).toString();

                String name = JOptionPane.showInputDialog(
                    this, "Enter Supplier Name:",
                    model.getValueAt(selectedRow, 1)
                );

                if (name == null || name.trim().isEmpty()) {
                    return;
                }

                String person = JOptionPane.showInputDialog(
                    this, "Enter Contact Person:",
                    model.getValueAt(selectedRow, 2)
                );

                if (person == null || person.trim().isEmpty()) {
                    return;
                }

                String phone = JOptionPane.showInputDialog(
                    this, "Enter Phone Number:",
                    model.getValueAt(selectedRow, 3)
                );

                if (phone == null || phone.trim().isEmpty()) {
                    return;
                }

                String email = JOptionPane.showInputDialog(
                    this, "Enter Email:",
                    model.getValueAt(selectedRow, 4)
                );

                if (email == null || email.trim().isEmpty()) {
                    return;
                }

                String address = JOptionPane.showInputDialog(
                    this, "Enter Address:",
                    model.getValueAt(selectedRow, 5)
                );

                if (address == null || address.trim().isEmpty()) {
                    return;
                }

                boolean updated = updateSupplier(
                    id, name.trim(), person.trim(),
                    phone.trim(), email.trim(), address.trim()
                );

                if (updated) {

                    loadSuppliers();

                    JOptionPane.showMessageDialog(
                        this, "Supplier updated successfully!"
                    );
                }
            }

            if (e.getSource() == btnDelete) {

                int selectedRow =
                    tblSuppliers.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(
                        this, "Please select a supplier first."
                    );
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this supplier?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {

                    String id = model.getValueAt(
                        selectedRow, 0
                    ).toString();

                    boolean deleted = deleteSupplier(id);

                    if (deleted) {

                        loadSuppliers();

                        JOptionPane.showMessageDialog(
                            this, "Supplier deleted successfully!"
                        );
                    }
                }
            }

            if (e.getSource() == btnSearch) {

                String search =
                    txtSearch.getText().trim();

                boolean found = false;

                for (int i = 0;
                     i < model.getRowCount(); i++) {

                    String id = model.getValueAt(i, 0).toString();

                    String name = model.getValueAt(i, 1).toString();

                    String person = model.getValueAt(i, 2).toString();

                    if (id.equalsIgnoreCase(search) ||
                        name.toLowerCase().contains(
                            search.toLowerCase()) ||
                        person.toLowerCase().contains(
                            search.toLowerCase())) {

                        tblSuppliers.setRowSelectionInterval(i, i);
                        found = true;
                        break;
                    }
                }

                if (found) {
                    JOptionPane.showMessageDialog(
                        this, "Supplier found!"
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this, "Supplier not found."
                    );
                }
            }

            if (e.getSource() == btnBack) {
                dispose();
            }
        }

        private void loadSuppliers() {

            model.setRowCount(0);

            String sql =
                "SELECT supplier_id, supplier_name, " +
                "contact_person, phone, email, address " +
                "FROM suppliers";

            try (Connection con =
                     DatabaseConnection.getConnection();
                 PreparedStatement ps =
                     con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    model.addRow(new Object[] {
                        rs.getLong("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("contact_person"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                    });
                }

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Unable to load suppliers: " + ex.getMessage()
                );
            }
        }

        private boolean saveSupplier(
                String id,
                String name,
                String person,
                String phone,
                String email,
                String address) {

            String sql =
                "INSERT INTO suppliers " +
                "(supplier_id, supplier_name, contact_person, " +
                "phone, email, address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection con =
                     DatabaseConnection.getConnection();
                 PreparedStatement ps =
                     con.prepareStatement(sql)) {

                ps.setLong(1, Long.parseLong(id));
                ps.setString(2, name);
                ps.setString(3, person);
                ps.setString(4, phone);
                ps.setString(5, email);
                ps.setString(6, address);

                ps.executeUpdate();

                return true;

            } catch (SQLException |
                     NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Unable to save supplier: " + ex.getMessage()
                );

                return false;
            }
        }

        private boolean updateSupplier(
                String id,
                String name,
                String person,
                String phone,
                String email,
                String address) {

            String sql =
                "UPDATE suppliers SET " +
                "supplier_name = ?, contact_person = ?, " +
                "phone = ?, email = ?, address = ? " +
                "WHERE supplier_id = ?";

            try (Connection con =
                     DatabaseConnection.getConnection();
                 PreparedStatement ps =
                     con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, person);
                ps.setString(3, phone);
                ps.setString(4, email);
                ps.setString(5, address);
                ps.setLong(6, Long.parseLong(id));

                return ps.executeUpdate() > 0;

            } catch (SQLException |
                     NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Unable to update supplier: " + ex.getMessage()
                );

                return false;
            }
        }

        private boolean deleteSupplier(String id) {

            String sql =
                "DELETE FROM suppliers WHERE supplier_id = ?";

            try (Connection con =
                     DatabaseConnection.getConnection();
                 PreparedStatement ps =
                     con.prepareStatement(sql)) {

                ps.setLong(1, Long.parseLong(id));

                return ps.executeUpdate() > 0;

            } catch (SQLException |
                     NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete supplier: " + ex.getMessage()
                );

                return false;
            }
        }
    }

