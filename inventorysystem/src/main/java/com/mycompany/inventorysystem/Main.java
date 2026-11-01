
package com.mycompany.inventorysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedList;

public class Main extends JFrame implements ActionListener {

    private JLabel lblTitle, lblTotal;
    private JTextField txtSearch;
    private JTable tblProducts;
    private DefaultTableModel model;
    private javax.swing.table.TableRowSorter<DefaultTableModel> productSorter;

    private JButton btnSuppliers;
    private JButton btnReports, btnAdd, btnUpdate;
    private JButton btnDelete, btnLogout;

    private JPanel sidebar, content;
    
    private JTable tblSuppliers;
    private DefaultTableModel supplierModel;

    private JTextField txtSupplierSearch;

    private JButton btnAddSupplier;
    private JButton btnUpdateSupplier;
    private JButton btnDeleteSupplier;
    private JButton btnSearchSupplier;
    
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

        btnSuppliers = new JButton("Suppliers");
        btnSuppliers.setBounds(10, 20, 160, 35);
        sidebar.add(btnSuppliers);

        btnReports = new JButton("Reports");
        btnReports.setBounds(10, 65, 160, 35);
        sidebar.add(btnReports);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(10, 485, 160, 35);
        sidebar.add(btnLogout);

        content = new JPanel();
        content.setLayout(null);
        content.setBounds(200, 60, 770, 540);
        add(content);

        JLabel lblProductManagement = new JLabel("Product Management");
        lblProductManagement.setBounds(20, 10, 250, 30);
        content.add(lblProductManagement);

        lblTotal = new JLabel("Total Products: 0");
        lblTotal.setBounds(20, 45, 200, 30);
        content.add(lblTotal);

        txtSearch = new JTextField();
        txtSearch.setBounds(20, 90, 300, 30);
        txtSearch.setToolTipText("Search by Product ID or Product Name");
        content.add(txtSearch);
        JLabel searchLabel = new JLabel("Search by ID or name");
        searchLabel.setBounds(335, 90, 200, 30);
        content.add(searchLabel);

        btnAdd = new JButton("Add Product");
        btnAdd.setBounds(20, 135, 120, 30);
        content.add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 135, 100, 30);
        content.add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(260, 135, 100, 30);
        content.add(btnDelete);

        String[] columns = {
            "ID", "Product Name", "Category",
            "Quantity", "Price", "Supplier ID"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return Double.class;
                if (column == 3) return Integer.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProducts = new JTable(model);
        productSorter = new javax.swing.table.TableRowSorter<>(model);
        productSorter.setSortsOnUpdates(true);
        // IDs remain strings for existing database methods, but sort numerically.
        Comparator<String> idComparator = (left, right) -> {
            if (left.isEmpty() || right.isEmpty()) return left.compareTo(right);
            return Long.compare(Long.parseLong(left), Long.parseLong(right));
        };
        productSorter.setComparator(0, idComparator);
        productSorter.setComparator(5, idComparator);
        tblProducts.setRowSorter(productSorter);
        javax.swing.table.DefaultTableCellRenderer textRenderer =
            new javax.swing.table.DefaultTableCellRenderer();
        textRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        javax.swing.table.DefaultTableCellRenderer numberRenderer =
            new javax.swing.table.DefaultTableCellRenderer();
        numberRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int column = 0; column < model.getColumnCount(); column++) {
            tblProducts.getColumnModel().getColumn(column).setCellRenderer(
                column == 1 || column == 2 ? textRenderer : numberRenderer);
        }
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
        });
        // Format only the display; editing, sorting and database writes use numbers.
        tblProducts.getColumnModel().getColumn(4).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                private final java.text.DecimalFormat pesoFormat = new java.text.DecimalFormat(
                    "\u20B1#,##0.00", java.text.DecimalFormatSymbols.getInstance(Locale.US));

                @Override
                protected void setValue(Object value) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setText(value instanceof Number ? pesoFormat.format(value)
                        : value == null ? "" : value.toString());
                }
            });

        JScrollPane scrollPane = new JScrollPane(tblProducts);
        scrollPane.setBounds(20, 185, 730, 335);
        content.add(scrollPane);

        btnSuppliers.addActionListener(this);
        btnReports.addActionListener(this);
        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
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

        if (e.getSource() == btnSuppliers) {
        new SupplierManagement();
}
        
        if (e.getSource() == btnReports) {
            new Reports();
        }
        if (e.getSource() == btnAdd) {
            AddProductDialog dialog = new AddProductDialog(this,
                (id, name, category, quantity, price, supplierID) -> {
                    for (String[] product : linkedList) {
                        if (Long.parseLong(product[0]) == Long.parseLong(id)) {
                            JOptionPane.showMessageDialog(this, "Product ID already exists!");
                            return false;
                        }
                    }

                    if (!saveProduct(id, name, category, quantity, price, supplierID)) {
                        return false;
                    }

                    linkedList.add(new String[]{
                        id, name, category, String.valueOf(quantity),
                        String.valueOf(price), supplierID
                    });
                    model.addRow(new Object[]{
                        id, name, category, quantity, price, supplierID
                    });
                    lblTotal.setText("Total Products: " + linkedList.size());
                    return true;
                });
            dialog.setVisible(true);
        }

        if (e.getSource() == btnUpdate) {
            int viewRow = tblProducts.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to update.");
                return;
            }
            int row = tblProducts.convertRowIndexToModel(viewRow);
            String[] product = new String[6];
            for (int column = 0; column < product.length; column++) {
                Object value = model.getValueAt(row, column);
                product[column] = value == null ? "" : value.toString();
            }
            UpdateProductDialog dialog = new UpdateProductDialog(this,
                (id, name, category, quantity, price, supplierID) -> {
                    if (!updateProduct(id, name, category, quantity, price, supplierID)) {
                        JOptionPane.showMessageDialog(this,
                            "Product was not updated. Check the ID and details.");
                        return false;
                    }
                    String[] updated = {id, name, category, String.valueOf(quantity),
                        String.valueOf(price), supplierID};
                    for (int i = 0; i < linkedList.size(); i++) {
                        if (linkedList.get(i)[0].equals(id)) {
                            linkedList.set(i, updated);
                            break;
                        }
                    }
                    Object[] values = {id, name, category, quantity, price, supplierID};
                    for (int column = 0; column < values.length; column++) {
                        model.setValueAt(values[column], row, column);
                    }
                    return true;
                }, product);
            dialog.setVisible(true);
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
                tblProducts.convertRowIndexToModel(selectedRow), 0
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

    private void filterProducts() {
        String search = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        if (search.isEmpty()) {
            productSorter.setRowFilter(null);
            return;
        }
        productSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String id = entry.getStringValue(0);
                String name = entry.getStringValue(1).toLowerCase(Locale.ROOT);
                return id.equalsIgnoreCase(search) || name.contains(search);
            }
        });
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

            try (java.sql.PreparedStatement check = con.prepareStatement(
                    "SELECT product_id FROM products WHERE product_id = ?")) {
                check.setLong(1, Long.parseLong(id));
                try (java.sql.ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Product ID already exists!");
                        return false;
                    }
                }
            }

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
 
