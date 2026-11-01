package com.mycompany.inventorysystem;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

    public AddProductDialog(JFrame owner, ProductSaver saver) {
        this(owner, saver, null);
    }

    // Update reuses the same fields, categories and validation as Add.
    protected AddProductDialog(JFrame owner, ProductSaver saver, String[] product) {
        super(owner, product == null ? "Add Product" : "Update Product", true);
        updating = product != null;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel fields = new JPanel(new GridLayout(0, 2, 10, 10));
        fields.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        addField(fields, "Product ID", txtID);
        addField(fields, "Product Name", txtName);
        JPanel categoryPanel = new JPanel(new BorderLayout(0, 6));
        categoryPanel.add(categoryBox, BorderLayout.NORTH);
        categoryPanel.add(txtCategory, BorderLayout.SOUTH);
        txtCategory.setVisible(false);
        txtCategory.setToolTipText("Enter a custom category");
        categoryBox.addActionListener(e -> {
            txtCategory.setVisible("Others".equals(categoryBox.getSelectedItem()));
            pack();
        });
        fields.add(new JLabel("Category:"));
        fields.add(categoryPanel);
        addField(fields, "Quantity", txtQuantity);
        JPanel pricePanel = new JPanel(new BorderLayout(6, 0));
        pricePanel.add(new JLabel("\u20B1"), BorderLayout.WEST);
        pricePanel.add(txtPrice, BorderLayout.CENTER);
        fields.add(new JLabel("Price:"));
        fields.add(pricePanel);
        addField(fields, "Supplier ID", txtSupplierID);
        add(fields, BorderLayout.CENTER);

        JButton btnCancel = new JButton("Cancel");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        buttons.add(btnSave);
        buttons.add(btnCancel);
        add(buttons, BorderLayout.SOUTH);

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

    private void addField(JPanel panel, String label, JTextField field) {
        JLabel fieldLabel = new JLabel(label + ":");
        fieldLabel.setLabelFor(field);
        panel.add(fieldLabel);
        panel.add(field);
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
            price = Double.parseDouble(txtPrice.getText().trim());
            if (!Double.isFinite(price) || price < 0 || price > 9999999999.99) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showError("Price must be a valid number between 0 and 9999999999.99.", txtPrice);
            return;
        }

        btnSave.setEnabled(false);
        boolean saved = saver.save(Long.toString(id), txtName.getText().trim(),
            category, quantity, price, Long.toString(supplierID));
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
