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
    private LinkedList<String[]> suppliers = new LinkedList<>();

    Main() {

        setTitle("Inventory Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

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

        setLocationRelativeTo(null);
        setVisible(true);
    }
    
     private void updateDashboard() {

        lblTotal.setText("Total Products: " + linkedList.size());

        int lowStock = 0;

        for (String[] product : linkedList) {

            int quantity = Integer.parseInt(product[3]);

            if (quantity <= 10) {
                lowStock++;
            }
        }

        lblLowStock.setText("Low Stock: " + lowStock);
     }
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnDashboard) {
            updateDashboard();
            

            String lowStockProducts = "";

            for (String[] product : linkedList) {

                int quantity = Integer.parseInt(product[3]);

                if (quantity <= 10) {

                    lowStockProducts +=
                        "ID: " + product[0]
                        + "\nProduct Name: " + product[1]
                        + "\nQuantity: " + product[3]
                        + "\n----------------------\n";
                }
            }
            if (lowStockProducts.isEmpty()) {

                JOptionPane.showMessageDialog(this, "No low-stock products.");
            } else {
                JOptionPane.showMessageDialog(this,lowStockProducts,"Low-Stock Products",JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
        if (e.getSource() == btnProducts) {
            JOptionPane.showMessageDialog(this, "Products");
        }

        if (e.getSource() == btnSuppliers) {
            JOptionPane.showMessageDialog(this, "Supplier Management");
        }  
        String[] options = {"Add Supplier", "View Suppliers", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this,"What would you like to do?","Suppliers",JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
            
            if (choice == 0) {
                String id = JOptionPane.showInputDialog(this, "Enter Supplier ID:");
                if (id == null || id.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter Supplier ID!");
                    return;
                }
            }
            
        String name = JOptionPane.showInputDialog(this, "Enter Supplier Name:");
                if (name == null || name.trim().isEmpty()) {JOptionPane.showMessageDialog(this, "Please enter Supplier Name!");
                    return;
                }
        String contact = JOptionPane.showInputDialog(this, "Enter Supplier Contact Information:");
        if (contact == null || contact.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter Supplier Contact Information!");
                    return;
                }
        String[] supplier = {
                    id.trim(), name.trim(), contact.trim()
                };
                suppliers.add(supplier);
                JOptionPane.showMessageDialog(this, "Supplier added successfully!");
                    
           else if (choice == 1) {

                String supplierList = "";

                for (String[] supplier : suppliers) {

                    supplierList +=
                        "Supplier ID: " + supplier[0]
                        + "\nName: " + supplier[1]
                        + "\nContact: " + supplier[2]
                        + "\n----------------------\n";
                }
                if (supplierList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No suppliers added yet.");
                } else {
                    JOptionPane.showMessageDialog(this,supplierList,"Supplier List",JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Product ID already exists!");
                return;
            }

            String name = JOptionPane.showInputDialog(this, "Enter Product Name:");

            if (name == null || name.trim().isEmpty()) {JOptionPane.showMessageDialog(
                    this, "Please enter Product Name!");
                return;
            }
             String category = JOptionPane.showInputDialog(this, "Enter Category:");

            if (category == null || category.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Category!");
                return;
            }
            
            String quantityInput = JOptionPane.showInputDialog(this, "Enter Quantity:");

            if (quantityInput == null || quantityInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Quantity!" );
                return;
            }

            int quantity;

            try {
                quantity = Integer.parseInt(quantityInput.trim());

                if (quantity < 0) {
                    JOptionPane.showMessageDialog(this, "Quantity cannot be negative!");
                    return;
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid whole number for Quantity!");
                return;
            }
            String priceInput = JOptionPane.showInputDialog(this, "Enter Price:");

            if (priceInput == null || priceInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Price!");
                return;
            }

            double price;

            try {
                price = Double.parseDouble(priceInput.trim());

                if (price < 0) {
                    JOptionPane.showMessageDialog(this, "Price cannot be negative!" );
                    return;
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for Price!");
                return;
            }
            
             String supplierID = JOptionPane.showInputDialog(this, "Enter Supplier ID:");

            if (supplierID == null || supplierID.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Supplier ID!");
                return;
            }
 
            String[] product = {
                id.trim(), name.trim(), category.trim(), String.valueOf(quantity),String.valueOf(price),supplierID.trim()

            };
                
            linkedList.add(product);
            
            model.addRow(new Object[]{  
                id.trim(),
                name.trim(),
                category.trim(), 
                quantity, 
                price,
                supplierID.trim()
            });

            lblTotal.setText("Total Products: " + linkedList.size());

            JOptionPane.showMessageDialog(this, "Product added successfully!");
        }
        
        if (e.getSource() == btnUpdate) {
            JOptionPane.showMessageDialog(this, "Update Product");
        }
        if (e.getSource() == btnDelete) {
            

            int selectedRow = tblProducts.getSelectedRow();

            if (selectedRow != -1) {

                linkedList.remove(selectedRow);
                model.removeRow(selectedRow);

                lblTotal.setText(
                    "Total Products: " + linkedList.size()
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, "Please select a product."
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

    }