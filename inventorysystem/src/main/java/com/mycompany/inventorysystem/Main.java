
package com.mycompany.inventorysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.awt.*;

public class Main extends JFrame implements ActionListener {

    private JLabel lblTitle, lblTotal, lblLowStock;
    private JTextField txtSearch;
    private JTable tblProducts;
    private DefaultTableModel model;

    private JButton btnDashboard, btnProducts, btnSuppliers;
    private JButton btnReports, btnAdd, btnUpdate;
    private JButton btnDelete, btnSearch, btnLogout;

    private JPanel sidebar, content;
    private LinkedList<String[]> linkedList = new LinkedList<>();

    Main() {

        setTitle("Inventory Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
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


        lblTitle = new JLabel("INVENTORY MANAGEMENT SYSTEM");
        lblTitle.setBounds(250, 15, 400, 30);
        add(lblTitle);

        sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBounds(10, 60, 180, 540);
        add(sidebar);

        btnDashboard = new JButton("Dashboard");
        btnDashboard.setBounds(10, 20, 160, 35);
        sidebar.add(btnDashboard);

        btnProducts = new JButton("Products");
        btnProducts.setBounds(10, 65, 160, 35);
        sidebar.add(btnProducts);

        btnSuppliers = new JButton("Suppliers");
        btnSuppliers.setBounds(10, 110, 160, 35);
        sidebar.add(btnSuppliers);

        btnReports = new JButton("Reports");
        btnReports.setBounds(10, 155, 160, 35);
        sidebar.add(btnReports);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(10, 470, 160, 35);
        sidebar.add(btnLogout);

        content = new JPanel();
        content.setLayout(null);
        content.setBounds(200, 60, 770, 540);
        add(content);

        JLabel lblDashboard = new JLabel("Dashboard");
        lblDashboard.setBounds(20, 10, 200, 30);
        content.add(lblDashboard);

        lblTotal = new JLabel("Total Products: 0");
        lblTotal.setBounds(20, 50, 200, 40);
        content.add(lblTotal);

        lblLowStock = new JLabel("Low Stock: 0");
        lblLowStock.setBounds(250, 50, 200, 40);
        content.add(lblLowStock);

        txtSearch = new JTextField();
        txtSearch.setBounds(20, 110, 250, 30);
        content.add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(280, 110, 100, 30);
        content.add(btnSearch);

        btnAdd = new JButton("Add Product");
        btnAdd.setBounds(20, 160, 120, 30);
        content.add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 160, 100, 30);
        content.add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(260, 160, 100, 30);
        content.add(btnDelete);

        String[] columns = {
            "ID", "Product Name", "Category",
            "Quantity", "Price", "Supplier ID"
        };

        model = new DefaultTableModel(columns, 0);
        tblProducts = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(tblProducts);
        scrollPane.setBounds(20, 210, 730, 300);
        content.add(scrollPane);

        btnDashboard.addActionListener(this);
        btnProducts.addActionListener(this);
        btnSuppliers.addActionListener(this);
        btnReports.addActionListener(this);
        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnSearch.addActionListener(this);
        btnLogout.addActionListener(this);

        loadProducts();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnDashboard) {
            JOptionPane.showMessageDialog(this, "Dashboard");
        }

        if (e.getSource() == btnProducts) {
            JOptionPane.showMessageDialog(this, "Products");
        }

        if (e.getSource() == btnSuppliers) {
            JOptionPane.showMessageDialog(this, "Suppliers");
        }

        if (e.getSource() == btnReports) {
            JOptionPane.showMessageDialog(this, "Reports");
        }
        if (e.getSource() == btnAdd) {

            String id = JOptionPane.showInputDialog(
                this, "Enter Product ID:"
            );

            if (id == null || id.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Please enter Product ID!"
                );
                return;
            } 
            boolean exists = false;

            for (String[] product : linkedList) {
                if (product[0].equalsIgnoreCase(id.trim())) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                JOptionPane.showMessageDialog(
                    this, "Product ID already exists!"
                );
                return;
            }

            String name = JOptionPane.showInputDialog(
                this, "Enter Product Name:"
            );

            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Please enter Product Name!"
                );
                return;
            }
             String category = JOptionPane.showInputDialog(
                this, "Enter Category:"
            );

            if (category == null || category.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Please enter Category!"
                );
                return;
            }
            
            String quantityInput = JOptionPane.showInputDialog(
                this, "Enter Quantity:"
            );

            if (quantityInput == null || quantityInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Please enter Quantity!"
                );
                return;
            }

            int quantity;

            try {
                quantity = Integer.parseInt(quantityInput.trim());

                if (quantity < 0) {
                    JOptionPane.showMessageDialog(
                        this, "Quantity cannot be negative!"
                    );
                    return;
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this, "Please enter a valid whole number for Quantity!"
                );
                return;
            }
            String priceInput = JOptionPane.showInputDialog(
                this, "Enter Price:"
            );

            if (priceInput == null || priceInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Please enter Price!"
                );
                return;
            }

            double price;

            try {
                price = Double.parseDouble(priceInput.trim());

                if (price < 0) {
                    JOptionPane.showMessageDialog(
                        this, "Price cannot be negative!"
                    );
                    return;
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this, "Please enter a valid number for Price!"
                    );
                    return;
                }

                 String supplierID = JOptionPane.showInputDialog(
                    this, "Enter Supplier ID:"
                );

                if (supplierID == null || supplierID.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                        this, "Please enter Supplier ID!"
                    );
                    return;
                }

                String[] product = {
                    id.trim(), name.trim(), category.trim(), String.valueOf(quantity),String.valueOf(price),supplierID.trim()

                };

                
              boolean saved = saveProduct(
                id.trim(),
                name.trim(),
                category.trim(),
                quantity,
                price,
                supplierID.trim()
                );

              if (saved) {
              linkedList.add(new String []{
                id.trim(),
                name.trim(),
                category.trim(),
                String.valueOf(quantity),
                String.valueOf(price),
                supplierID.trim()
                });

              linkedList.add(product);

              model.addRow(new Object[]{
                 id.trim(),
                 name.trim(),
                 category.trim(),
                 quantity,
                 price,
                 supplierID.trim()
                 });

              lblTotal.setText(
                "Total Products: " + linkedList.size()
                 );

                JOptionPane.showMessageDialog(
                this, "Product added successfully!"
                 );
                }

        }
        
        
        if (e.getSource() == btnUpdate) {

    int selectedRow = tblProducts.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
            this,
            "Please select a product to update."
        );
        return;
    }

    String id = model.getValueAt(
        selectedRow, 0
    ).toString();

    String name = JOptionPane.showInputDialog(
        this, "Enter Product Name:",
        model.getValueAt(selectedRow, 1)
    );

    if (name == null || name.trim().isEmpty()) {
        return;
    }

    String category = JOptionPane.showInputDialog(
        this, "Enter Category:",
        model.getValueAt(selectedRow, 2)
    );

    if (category == null || category.trim().isEmpty()) {
        return;
    }

    String quantityInput = JOptionPane.showInputDialog(
        this, "Enter Quantity:",
        model.getValueAt(selectedRow, 3)
    );

    if (quantityInput == null ||
        quantityInput.trim().isEmpty()) {
        return;
    }

    int quantity;

    try {
        quantity = Integer.parseInt(quantityInput.trim());

        if (quantity < 0) {
            JOptionPane.showMessageDialog(
                this, "Quantity cannot be negative!"
            );
            return;
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(
            this, "Enter a valid whole number for Quantity!"
        );
        return;
    }

    String priceInput = JOptionPane.showInputDialog(
        this, "Enter Price:",
        model.getValueAt(selectedRow, 4)
    );

    if (priceInput == null || priceInput.trim().isEmpty()) {
        return;
    }

    double price;

    try {
        price = Double.parseDouble(priceInput.trim());

        if (!Double.isFinite(price) || price < 0) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid non-negative Price!"
            );
            return;
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(
            this, "Enter a valid number for Price!"
        );
        return;
    }

    String supplierID = JOptionPane.showInputDialog(
        this, "Enter Supplier ID:",
        model.getValueAt(selectedRow, 5)
    );

    if (supplierID == null || supplierID.trim().isEmpty()) {
        return;
    }

    try {
        Long.parseLong(supplierID.trim());
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(
            this, "Supplier ID must be a whole number."
        );
        return;
    }

    boolean updated = updateProduct(
        id,
        name.trim(),
        category.trim(),
        quantity,
        price,
        supplierID.trim()
        );

    if (updated) {
        loadProducts();

        JOptionPane.showMessageDialog(
            this, "Product updated successfully!"
        );
       }   
    else {
        JOptionPane.showMessageDialog(
            this,
            "Product was not updated. Check the ID and details."
            );
        }
    }


        
        if (e.getSource() == btnDelete) {

            int selectedRow = tblProducts.getSelectedRow();

            if (selectedRow != -1) {

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this product?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            String id = model.getValueAt(
                selectedRow, 0
            ).toString();

            boolean deleted = deleteProduct(id);

            if (deleted) {
                loadProducts();

                JOptionPane.showMessageDialog(
                    this,
                    "Product deleted successfully!"
                );
            }
        }

    } else {
        JOptionPane.showMessageDialog(
            this,
            "Please select a product first."
        );
      }
    }

        if (e.getSource() == btnSearch) {

            String search = txtSearch.getText().trim();
            boolean found = false;

            for (int i = 0; i < model.getRowCount(); i++) {

                String id = model.getValueAt(i, 0).toString();

                if (id.equalsIgnoreCase(search)) {
                    tblProducts.setRowSelectionInterval(i, i);
                    found = true;
                    break;
                }
            }

            if (found) {
                JOptionPane.showMessageDialog(
                    this, "Product found!"
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, "Product not found."
                );
            }
        }

        if (e.getSource() == btnLogout) {
            dispose();
        }
    }

     private void loadProducts() {

        model.setRowCount(0);
        linkedList.clear();

        String sql = "SELECT product_id, product_name, " +
            "category, quantity, price, supplier_id " +
            "FROM products";

        try (java.sql.Connection con =
                 DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps =
                 con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String id = rs.getString("product_id");
                String name = rs.getString("product_name");
                String category = rs.getString("category");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                Object supplierValue =
                    rs.getObject("supplier_id");

                String supplierID =
                    supplierValue == null
                        ? ""
                        : supplierValue.toString();

                model.addRow(new Object[]{
                    id, name, category,
                    quantity, price, supplierID
                });

                linkedList.add(new String[]{
                    id, name, category,
                    String.valueOf(quantity),
                    String.valueOf(price), supplierID
                });
            }

            lblTotal.setText(
                "Total Products: " + model.getRowCount()
            );

        } catch (java.sql.SQLException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to load products: " + ex.getMessage()
            );
        }
    }
    
     
    private boolean updateProduct(
            String id,
            String name,
            String category,
            int quantity,
            double price,
            String supplierID) {

        String sql = "UPDATE products SET " +
        "product_name = ?, category = ?, " +
        "quantity = ?, price = ?, supplier_id = ? " +
        "WHERE product_id = ?";

    try (java.sql.Connection con =
             DatabaseConnection.getConnection();
         java.sql.PreparedStatement ps =
             con.prepareStatement(sql)) {

        ps.setString(1, name);
        ps.setString(2, category);
        ps.setInt(3, quantity);
        ps.setDouble(4, price);
        ps.setLong(5, Long.parseLong(supplierID));
        ps.setLong(6, Long.parseLong(id));

        int rowsUpdated = ps.executeUpdate();

        return rowsUpdated > 0;

    } catch (java.sql.SQLException |
             NumberFormatException ex) {

        JOptionPane.showMessageDialog(
            this,
            "Unable to update product: " + ex.getMessage()
        );

        return false;
    }
}

     
     private boolean deleteProduct(String id) {

    String sql = "DELETE FROM products WHERE product_id = ?";

    try (java.sql.Connection con =
             DatabaseConnection.getConnection();
         java.sql.PreparedStatement ps =
             con.prepareStatement(sql)) {

        ps.setLong(1, Long.parseLong(id));

        int rowsDeleted = ps.executeUpdate();

        return rowsDeleted > 0;

    } catch (java.sql.SQLException |
             NumberFormatException ex) {

        JOptionPane.showMessageDialog(
            this,
            "Unable to delete product: " + ex.getMessage()
        );

        return false;
    }
}
     
    private boolean saveProduct(
            String id,
            String name,
            String category,
            int quantity,
            double price,
            String supplierID) {

        String sql = "INSERT INTO products " +
            "(product_id, product_name, category, " +
            "quantity, price, supplier_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection con =
                 DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps =
                 con.prepareStatement(sql)) {

            ps.setLong(1, Long.parseLong(id));
            ps.setString(2, name);
            ps.setString(3, category);
            ps.setInt(4, quantity);
            ps.setDouble(5, price);
            ps.setLong(6, Long.parseLong(supplierID));

            ps.executeUpdate();
            return true;

        } catch (java.sql.SQLException |
                 NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to save product: " + ex.getMessage()
            );
            return false;
        }
    } 
} 
