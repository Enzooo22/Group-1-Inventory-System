
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
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;

    private JButton btnAdd, btnUpdate;
    private JButton btnDelete, btnBack;

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
        txtSearch.setToolTipText("Search by Supplier ID or Supplier Name");
        JLabel searchLabel = new JLabel("Search by Supplier ID or Name");
        searchLabel.setBounds(300, 80, 300, 30);
        add(searchLabel);

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
            "Contact Number",
            "Email Address",
            "Address"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Long.class : String.class;
            }
        };
        tblSuppliers = new JTable(model);
        sorter = new javax.swing.table.TableRowSorter<>(model);
        sorter.setSortsOnUpdates(true);
        tblSuppliers.setRowSorter(sorter);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterSuppliers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterSuppliers(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterSuppliers(); }
        });

        JScrollPane scrollPane =
        new JScrollPane(tblSuppliers);

        scrollPane.setBounds(30, 190, 870, 330);
        add(scrollPane);

        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);

        btnBack.addActionListener(this);

        loadSuppliers();

        setLocationRelativeTo(null);
        setVisible(true);
    }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == btnAdd || e.getSource() == btnUpdate) {
                boolean updating = e.getSource() == btnUpdate;
                String[] supplier = null;
                if (updating) {
                    int selected = tblSuppliers.getSelectedRow();
                    if (selected == -1) {
                        JOptionPane.showMessageDialog(this, "Please select a supplier to update.");
                        return;
                    }
                    int row = tblSuppliers.convertRowIndexToModel(selected);
                    supplier = new String[6];
                    for (int column = 0; column < supplier.length; column++) {
                        Object value = model.getValueAt(row, column);
                        supplier[column] = value == null ? "" : value.toString();
                    }
                }
                SupplierDialog dialog = new SupplierDialog(this, supplier, values -> {
                    boolean saved = updating
                        ? updateSupplier(values[0], values[1], values[2], values[3], values[4], values[5])
                        : saveSupplier(values[0], values[1], values[2], values[3], values[4], values[5]);
                    if (saved) loadSuppliers();
                    return saved;
                });
                dialog.setVisible(true);
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
                        tblSuppliers.convertRowIndexToModel(selectedRow), 0
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

            if (e.getSource() == btnBack) {
                dispose();
            }
        }

        private void filterSuppliers() {
            String search = txtSearch.getText().trim().toLowerCase(java.util.Locale.ROOT);
            if (search.isEmpty()) {
                sorter.setRowFilter(null);
                return;
            }
            sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    return entry.getStringValue(0).equals(search)
                        || entry.getStringValue(1).toLowerCase(java.util.Locale.ROOT).contains(search);
                }
            });
        }

        private void loadSuppliers() {

            java.util.List<Object[]> rows = new java.util.ArrayList<>();

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

                    rows.add(new Object[] {
                        rs.getLong("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("contact_person"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                    });
                }
                model.setRowCount(0);
                for (Object[] row : rows) model.addRow(row);

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

                try (PreparedStatement check = con.prepareStatement(
                        "SELECT supplier_id FROM suppliers WHERE supplier_id = ?")) {
                    check.setLong(1, Long.parseLong(id));
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this, "Supplier ID already exists!");
                            return false;
                        }
                    }
                }
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

                if (ps.executeUpdate() > 0) return true;
                JOptionPane.showMessageDialog(this, "Supplier was not updated. It may no longer exist.");
                return false;

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
                "DELETE FROM suppliers WHERE supplier_id = ? " +
                "AND NOT EXISTS (SELECT 1 FROM products WHERE products.supplier_id = suppliers.supplier_id)";

            try (Connection con =
                     DatabaseConnection.getConnection();
                 PreparedStatement ps =
                     con.prepareStatement(sql)) {

                try (PreparedStatement check = con.prepareStatement(
                        "SELECT 1 FROM products WHERE supplier_id = ? LIMIT 1")) {
                    check.setLong(1, Long.parseLong(id));
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this,
                                "Cannot delete this supplier because existing products still use it.");
                            return false;
                        }
                    }
                }
                // Recheck references in the DELETE itself as well as above.
                ps.setLong(1, Long.parseLong(id));
                if (ps.executeUpdate() > 0) return true;
                JOptionPane.showMessageDialog(this,
                    "Supplier was not deleted. It may be in use or no longer exist.");
                return false;

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

