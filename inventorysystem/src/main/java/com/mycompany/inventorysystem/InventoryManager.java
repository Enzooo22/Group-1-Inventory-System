package com.mycompany.inventorysystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class InventoryManager {
    private final LinkedList<Product> products = new LinkedList<>();

    // The UI can read the inventory without changing the manager's list structure.
    public LinkedList<Product> getProducts() {
        return new LinkedList<>(products);
    }

    public void loadProducts() throws SQLException {
        LinkedList<Product> loaded = new LinkedList<>();
        String sql = "SELECT product_id, product_name, " +
            "category, quantity, price, supplier_id " +
            "FROM products";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long supplierValue = rs.getLong("supplier_id");
                Long supplierId = rs.wasNull() ? null : Long.valueOf(supplierValue);
                loaded.add(new Product(rs.getLong("product_id"), rs.getString("product_name"),
                    rs.getString("category"), rs.getInt("quantity"), supplierId, rs.getDouble("price")));
            }
        }
        // A failed query (or resource close) leaves the previous inventory intact.
        products.clear();
        products.addAll(loaded);
    }

    public boolean addProduct(Product product) throws SQLException {
        for (Product existing : products) {
            if (existing.getProductId() == product.getProductId()) {
                throw new IllegalArgumentException("Product ID already exists!");
            }
        }
        String sql = "INSERT INTO products " +
            "(product_id, product_name, category, " +
            "quantity, price, supplier_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            try (PreparedStatement check = con.prepareStatement(
                    "SELECT product_id FROM products WHERE product_id = ?")) {
                check.setLong(1, product.getProductId());
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) throw new IllegalArgumentException("Product ID already exists!");
                }
            }
            requireSupplier(con, product.getSupplierId());
            ps.setLong(1, product.getProductId());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getCategory());
            ps.setInt(4, product.getQuantity());
            ps.setDouble(5, product.getPrice());
            ps.setLong(6, product.getSupplierId());
            if (ps.executeUpdate() == 0) return false;
        }
        products.add(product);
        return true;
    }

    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET " +
            "product_name = ?, category = ?, " +
            "quantity = ?, price = ?, supplier_id = ? " +
            "WHERE product_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            requireSupplier(con, product.getSupplierId());
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setDouble(4, product.getPrice());
            ps.setLong(5, product.getSupplierId());
            ps.setLong(6, product.getProductId());
            if (ps.executeUpdate() == 0) return false;
        }
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId() == product.getProductId()) {
                products.set(i, product);
                break;
            }
        }
        return true;
    }

    public boolean deleteProduct(long id) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0) return false;
        }
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId() == id) {
                products.remove(i);
                break;
            }
        }
        return true;
    }

    private void requireSupplier(Connection con, Long supplierId) throws SQLException {
        if (supplierId != null) {
            try (PreparedStatement check = con.prepareStatement(
                    "SELECT supplier_id FROM suppliers WHERE supplier_id = ?")) {
                check.setLong(1, supplierId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) return;
                }
            }
        }
        throw new IllegalArgumentException(
            "Supplier ID does not exist. Please choose an existing supplier.");
    }
}
