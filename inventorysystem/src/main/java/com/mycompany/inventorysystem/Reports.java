package com.mycompany.inventorysystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Reports extends JFrame {

    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int TARGET_STOCK = 20;
    private final DefaultTableModel lowStockModel;
    private final DefaultTableModel restockModel;
    private final JLabel lowStockMessage = new JLabel("Loading inventory...");
    private final JLabel restockMessage = new JLabel("Loading inventory...");
    private final JLabel refreshMessage = new JLabel(" ");
    private boolean showingError;

    public Reports() {
        setTitle("Inventory Reports");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setSize(1050, 740);
        setResizable(false);

        JLabel title = new JLabel("LOW-STOCK AND RESTOCK REPORTS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBounds(250, 15, 600, 30);
        add(title);
        JButton refresh = new JButton("Refresh");
        refresh.setBounds(890, 15, 120, 30);
        refresh.setBackground(new Color(255, 120, 120));
        refresh.setForeground(new Color(35, 35, 35));
        refresh.addActionListener(e -> loadReports());
        add(refresh);

        JLabel lowStockTitle = new JLabel("Low Stock: 1-10 units | Out of Stock: 0 units");
        lowStockTitle.setBounds(20, 60, 900, 25);
        lowStockTitle.setFont(new Font("Arial", Font.BOLD, 12));
        add(lowStockTitle);
        lowStockModel = createModel(new String[]{"Product ID", "Product Name", "Category",
            "Quantity", "Supplier ID", "Stock Status"});
        addTable(lowStockModel, 90, 210);
        lowStockMessage.setBounds(20, 305, 990, 25);
        lowStockMessage.setFont(new Font("Arial", Font.BOLD, 13));
        add(lowStockMessage);

        JLabel restockTitle = new JLabel("Restock Report: products below 20 units | Suggested quantity = 20 - current quantity");
        restockTitle.setBounds(20, 350, 990, 25);
        restockTitle.setFont(new Font("Arial", Font.BOLD, 13));
        add(restockTitle);
        restockModel = createModel(new String[]{"Product ID", "Product Name", "Category",
            "Quantity", "Supplier ID", "Stock Status", "Suggested Restock Quantity"});
        addTable(restockModel, 380, 230);
        restockMessage.setBounds(20, 615, 990, 25);
        restockMessage.setFont(new Font("Arial", Font.BOLD, 13));
        add(restockMessage);
        refreshMessage.setBounds(20, 655, 990, 25);
        refreshMessage.setFont(new Font("Arial", Font.BOLD, 13));
        add(refreshMessage);

        // Also reload after returning from product edits in the main window.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                if (!showingError) loadReports();
            }
        });
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private DefaultTableModel createModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 4) return Long.class;
                if (column == 3 || column == 6) return Integer.class;
                return String.class;
            }
        };
    }

    private void addTable(DefaultTableModel model, int y, int height) {
        JTable table = new JTable(model);
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(198, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 205, 210));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(240, 180, 180));
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(125);
        if (model.getColumnCount() == 7) {
            table.getColumnModel().getColumn(6).setPreferredWidth(200);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, y, 990, height);
        add(scrollPane);
    }

    static String stockStatus(int quantity) {
        if (quantity == 0) return "Out of Stock";
        if (quantity <= LOW_STOCK_THRESHOLD) return "Low Stock";
        return "Below Target";
    }

    static int suggestedRestock(int quantity) {
        return Math.max(0, TARGET_STOCK - quantity);
    }

    private void loadReports() {
        List<Object[]> lowStockRows = new ArrayList<>();
        List<Object[]> restockRows = new ArrayList<>();
        String sql = "SELECT product_id, product_name, category, quantity, supplier_id "
            + "FROM products WHERE quantity >= 0 AND quantity < ? ORDER BY quantity, product_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, TARGET_STOCK);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("product_id");
                    String name = rs.getString("product_name");
                    String category = rs.getString("category");
                    int quantity = rs.getInt("quantity");
                    long supplierValue = rs.getLong("supplier_id");
                    Long supplierID = rs.wasNull() ? null : Long.valueOf(supplierValue);
                    String status = stockStatus(quantity);
                    if (quantity <= LOW_STOCK_THRESHOLD) {
                        lowStockRows.add(new Object[]{id, name, category, quantity, supplierID, status});
                    }
                    restockRows.add(new Object[]{id, name, category, quantity, supplierID,
                        status, suggestedRestock(quantity)});
                }
            }
            // Replace both reports only after the complete query succeeds.
            lowStockModel.setRowCount(0);
            restockModel.setRowCount(0);
            for (Object[] row : lowStockRows) lowStockModel.addRow(row);
            for (Object[] row : restockRows) restockModel.addRow(row);
            lowStockMessage.setText(lowStockRows.isEmpty()
                ? "No low-stock or out-of-stock products." : lowStockRows.size() + " product(s) at or below 10 units.");
            restockMessage.setText(restockRows.isEmpty()
                ? "No products need restocking. All products are at or above the target, or inventory is empty."
                : restockRows.size() + " product(s) need replenishment to reach 20 units.");
            refreshMessage.setText("Inventory loaded from the database. Reports are read-only.");
        } catch (SQLException ex) {
            refreshMessage.setText("Refresh failed. Displayed results may be outdated. Click Refresh to retry.");
            lowStockMessage.setText("Current stock information is unavailable; any displayed rows are from the last successful load.");
            restockMessage.setText("Current restock information is unavailable; any displayed rows are from the last successful load.");
            // Suppress another activation reload when this error dialog closes.
            showingError = true;
            JOptionPane.showMessageDialog(this, "Unable to load inventory reports: " + ex.getMessage(),
                "Reports Unavailable", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> showingError = false);
        }
    }
}
