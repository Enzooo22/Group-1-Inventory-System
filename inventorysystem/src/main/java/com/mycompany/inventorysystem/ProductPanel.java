package com.mycompany.inventorysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Locale;

public class ProductPanel extends JPanel implements ActionListener {

    private final JFrame owner;
    private JLabel lblTotal;
    private JTextField txtSearch;
    private JTable tblProducts;
    private DefaultTableModel model;
    private javax.swing.table.TableRowSorter<DefaultTableModel> productSorter;
    private JButton btnAdd, btnUpdate, btnDelete;
    private final InventoryManager inventoryManager = new InventoryManager();

    public ProductPanel(JFrame owner) {
        this.owner = owner;
        setLayout(null);
        JLabel lblProductManagement = new JLabel("Product Management");
        lblProductManagement.setBounds(20, 10, 250, 30);
        add(lblProductManagement);

        lblTotal = new JLabel("Total Products: 0");
        lblTotal.setBounds(20, 45, 200, 30);
        add(lblTotal);

        txtSearch = new JTextField();
        txtSearch.setBounds(20, 90, 300, 30);
        txtSearch.setToolTipText("Search by Product ID or Product Name");
        add(txtSearch);
        JLabel searchLabel = new JLabel("Search by ID or name");
        searchLabel.setBounds(335, 90, 200, 30);
        add(searchLabel);

        btnAdd = new JButton("Add Product");
        btnAdd.setBounds(20, 135, 120, 30);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 135, 100, 30);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(260, 135, 100, 30);
        add(btnDelete);

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
        add(scrollPane);

        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        loadProducts();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            AddProductDialog dialog = new AddProductDialog(owner,
                (id, name, category, quantity, price, supplierID) -> {
                    if (!saveProduct(id, name, category, quantity, price, supplierID)) {
                        return false;
                    }

                    model.addRow(new Object[]{
                        id, name, category, quantity, price, supplierID
                    });
                    lblTotal.setText("Total Products: " + inventoryManager.getProducts().size());
                    return true;
                });
            dialog.setVisible(true);
        }

        if (e.getSource() == btnUpdate) {
            int viewRow = tblProducts.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(owner, "Please select a product to update.");
                return;
            }
            int row = tblProducts.convertRowIndexToModel(viewRow);
            String[] product = new String[6];
            for (int column = 0; column < product.length; column++) {
                Object value = model.getValueAt(row, column);
                product[column] = value == null ? "" : value.toString();
            }
            UpdateProductDialog dialog = new UpdateProductDialog(owner,
                (id, name, category, quantity, price, supplierID) -> {
                    if (!updateProduct(id, name, category, quantity, price, supplierID)) {
                        JOptionPane.showMessageDialog(owner,
                            "Product was not updated. Check the ID and details.");
                        return false;
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
            owner,
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
                    owner,
                    "Product deleted successfully!"
                );
            }
        }

    } else {
        JOptionPane.showMessageDialog(
            owner,
            "Please select a product first."
        );
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
        try {
            inventoryManager.loadProducts();
            refreshTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(owner, "Unable to load products: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Product product : inventoryManager.getProducts()) {
            model.addRow(new Object[]{
                Long.toString(product.getProductId()), product.getProductName(), product.getCategory(),
                product.getQuantity(), product.getPrice(),
                product.getSupplierId() == null ? "" : product.getSupplierId().toString()
            });
        }
        lblTotal.setText("Total Products: " + model.getRowCount());
    }

    private boolean saveProduct(String id, String name, String category,
                                int quantity, double price, String supplierID) {
        try {
            return inventoryManager.addProduct(new Product(Long.parseLong(id), name, category,
                quantity, Long.valueOf(supplierID), price));
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(owner, "Unable to save product: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(owner, ex.getMessage());
        }
        return false;
    }

    private boolean updateProduct(String id, String name, String category,
                                  int quantity, double price, String supplierID) {
        try {
            return inventoryManager.updateProduct(new Product(Long.parseLong(id), name, category,
                quantity, Long.valueOf(supplierID), price));
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(owner, "Unable to update product: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(owner, ex.getMessage());
        }
        return false;
    }

    private boolean deleteProduct(String id) {
        try {
            boolean deleted = inventoryManager.deleteProduct(Long.parseLong(id));
            if (deleted) refreshTable();
            return deleted;
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(owner, "Unable to delete product: " + ex.getMessage());
            return false;
        }
    }
}
