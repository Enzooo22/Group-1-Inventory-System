package com.mycompany.inventorysystem;

import java.awt.Dimension;
import java.math.BigDecimal;
import javax.swing.*;

public class AddProductDialog extends JDialog {

    // Main owns database writes and inventory updates; false keeps this form open.
    public interface ProductSaver {
        boolean save(String id, String name, String category,
                     int quantity, double price, String supplierID);
    }

    private final JTextField txtID = new JTextField(22);
    private final JTextField txtName = new JTextField(22);
    private final JTextField txtCategory = new JTextField(22);
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{
        "Vegetables", "Fruits", "Meat & Poultry", "Fish & Seafood", "Rice & Grains",
        "Canned Goods", "Instant Noodles", "Snacks & Chips", "Candies & Sweets",
        "Biscuits & Cookies", "Bread & Bakery", "Dairy Products", "Frozen Foods",
        "Beverages", "Coffee & Tea", "Condiments & Sauces", "Cooking Essentials",
        "Personal Care", "Household Cleaning", "School & Office Supplies", "Others"
    });
    private final JTextField txtQuantity = new JTextField(22);
    private final JTextField txtPrice = new JTextField(22);
    private final JTextField txtSupplierID = new JTextField(22);
    private final JButton btnSave = new JButton("Save Product");
    private final boolean updating;
    private final JLabel[] fieldLabels = new JLabel[6];
    private final JLabel pesoLabel = new JLabel("\u20B1");
    private final JButton btnCancel = new JButton("Cancel");

    public AddProductDialog(JFrame owner, ProductSaver saver) {
        this(owner, saver, null);
    }

    // Update reuses the same fields, categories and validation as Add.
    protected AddProductDialog(JFrame owner, ProductSaver saver, String[] product) {
        super(owner, product == null ? "Add Product" : "Update Product", true);
        updating = product != null;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(null);
        addField("Product ID", txtID, 0);
        addField("Product Name", txtName, 1);
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setBounds(16, 100, 180, 30);
        categoryLabel.setLabelFor(categoryBox);
        add(categoryLabel);
        categoryBox.setBounds(206, 100, 260, 30);
        add(categoryBox);
        txtCategory.setBounds(206, 136, 260, 30);
        txtCategory.setVisible(false);
        txtCategory.setToolTipText("Enter a custom category");
        add(txtCategory);
        categoryBox.addActionListener(e -> positionFields());
        addField("Quantity", txtQuantity, 3);
        addField("Price", txtPrice, 4);

        pesoLabel.setBounds(206, 180, 20, 30);
        add(pesoLabel);
        addField("Supplier ID", txtSupplierID, 5);

        add(btnSave);
        add(btnCancel);
        btnCancel.setBounds(346, 266, 120, 30);
        positionFields();
        btnSave.addActionListener(e -> saveProduct(saver));
        btnCancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnSave);
        if (updating) {
            txtID.setText(product[0]);
            txtID.setEditable(false);
            txtName.setText(product[1]);
            String category = product[2] == null ? "" : product[2];
            boolean predefined = false;
            for (int i = 0; i < categoryBox.getItemCount() - 1; i++) {
                if (categoryBox.getItemAt(i).equals(category)) {
                    categoryBox.setSelectedIndex(i);
                    predefined = true;
                    break;
                }
            }
            if (!predefined) {
                categoryBox.setSelectedItem("Others");
                txtCategory.setText(category);
            }
            txtQuantity.setText(product[3]);
            txtPrice.setText(product[4]);
            txtSupplierID.setText(product[5]);
            btnSave.setText("Save Changes");
        }
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void addField(String label, JTextField field, int row) {
        JLabel fieldLabel = new JLabel(label + ":");
        fieldLabels[row] = fieldLabel;
        fieldLabel.setBounds(16, 20 + row * 40, 180, 30);
        fieldLabel.setLabelFor(field);
        add(fieldLabel);
        field.setBounds(206, 20 + row * 40, 260, 30);
        add(field);
    }

    private void positionFields() {
        boolean custom = "Others".equals(categoryBox.getSelectedItem());
        int offset = custom ? 40 : 0;
        txtCategory.setVisible(custom);
        fieldLabels[3].setBounds(16, 140 + offset, 180, 30);
        fieldLabels[4].setBounds(16, 180 + offset, 180, 30);
        fieldLabels[5].setBounds(16, 220 + offset, 180, 30);
        pesoLabel.setBounds(206, 180 + offset, 20, 30);
        btnCancel.setBounds(346, 266 + offset, 120, 30);
        txtQuantity.setBounds(206, 140 + offset, 260, 30);
        txtPrice.setBounds(230, 180 + offset, 236, 30);
        txtSupplierID.setBounds(206, 220 + offset, 260, 30);
        btnSave.setBounds(186, 266 + offset, 150, 30);
        getContentPane().setPreferredSize(new Dimension(482, 312 + offset));
        pack();
    }
    private void saveProduct(ProductSaver saver) {
        JTextField[] fields = {
            txtID, txtName, txtQuantity, txtPrice, txtSupplierID
        };
        String[] labels = {
            "Product ID", "Product Name", "Quantity", "Price", "Supplier ID"
        };
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getText().trim().isEmpty()) {
                showError("Please enter " + labels[i] + "!", fields[i]);
                return;
            }
        }

        String category = (String) categoryBox.getSelectedItem();
        if ("Others".equals(category)) {
            category = txtCategory.getText().trim();
            if (category.isEmpty()) {
                showError("Please enter a custom category!", txtCategory);
                return;
            }
        }

        String name = txtName.getText().trim();
        if (name.codePointCount(0, name.length()) > 150) {
            showError("Product Name must be at most 150 characters.", txtName);
            return;
        }
        if (category.codePointCount(0, category.length()) > 100) {
            showError("Category must be at most 100 characters.", txtCategory);
            return;
        }

        long id;
        long supplierID;
        int quantity;
        double price;
        try {
            id = Long.parseLong(txtID.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Product ID must be a whole number within the BIGINT range.", txtID);
            return;
        }
        try {
            supplierID = Long.parseLong(txtSupplierID.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Supplier ID must be a whole number within the BIGINT range.", txtSupplierID);
            return;
        }
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
            if (quantity < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showError("Quantity must be a whole number between 0 and 2147483647.", txtQuantity);
            return;
        }
        try {
            BigDecimal amount = new BigDecimal(txtPrice.getText().trim());
            if (amount.scale() > 2 || amount.signum() < 0
                    || amount.compareTo(new BigDecimal("9999999999.99")) > 0) {
                throw new NumberFormatException();
            }
            price = amount.doubleValue();
        } catch (NumberFormatException ex) {
            showError("Price must be between 0 and 9999999999.99 with at most two decimal places.", txtPrice);
            return;
        }

        btnSave.setEnabled(false);
        boolean saved;
        try {
            saved = saver.save(Long.toString(id), name,
                category, quantity, price, Long.toString(supplierID));
        } finally {
            btnSave.setEnabled(true);
        }
        if (saved) {
            dispose();
            JOptionPane.showMessageDialog(getOwner(), updating
                ? "Product updated successfully!" : "Product added successfully!");
        } else {
            btnSave.setEnabled(true);
        }
    }

    private void showError(String message, JTextField field) {
        JOptionPane.showMessageDialog(this, message, "Invalid Product", JOptionPane.ERROR_MESSAGE);
        field.requestFocusInWindow();
        field.selectAll();
    }
}
