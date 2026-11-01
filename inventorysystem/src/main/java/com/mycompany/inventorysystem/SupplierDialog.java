package com.mycompany.inventorysystem;

import javax.swing.*;
import java.awt.Dimension;

public class SupplierDialog extends JDialog {
    public interface SupplierSaver {
        boolean save(String[] values);
    }

    private final JTextField[] fields = new JTextField[6];
    private final String[] labels = {"Supplier ID", "Supplier Name", "Contact Person",
        "Contact Number", "Email Address", "Address"};

    public SupplierDialog(JFrame owner, String[] supplier, SupplierSaver saver) {
        super(owner, supplier == null ? "Add Supplier" : "Update Supplier", true);
        boolean updating = supplier != null;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        for (int i = 0; i < fields.length; i++) {
            JLabel label = new JLabel(labels[i] + (i < 2 ? " *:" : ":"));
            label.setBounds(20, 20 + i * 42, 140, 30);
            fields[i] = new JTextField(updating ? supplier[i] : "");
            fields[i].setBounds(165, 20 + i * 42, 295, 30);
            label.setLabelFor(fields[i]);
            add(label);
            add(fields[i]);
        }
        fields[0].setEditable(!updating);
        JLabel required = new JLabel("* Required fields");
        required.setBounds(20, 275, 160, 25);
        add(required);
        JButton save = new JButton(updating ? "Save Changes" : "Save Supplier");
        save.setBounds(180, 310, 150, 32);
        add(save);
        JButton cancel = new JButton("Cancel");
        cancel.setBounds(340, 310, 120, 32);
        add(cancel);
        cancel.addActionListener(e -> dispose());
        save.addActionListener(e -> {
            String[] values = new String[6];
            int[] limits = {20, 150, 100, 30, 150, 255};
            for (int i = 0; i < fields.length; i++) {
                values[i] = fields[i].getText().trim();
                if (i < 2 && values[i].isEmpty()) {
                    showError("Please enter " + labels[i] + ".", i);
                    return;
                }
                if (i > 0 && values[i].codePointCount(0, values[i].length()) > limits[i]) {
                    showError(labels[i] + " must be at most " + limits[i] + " characters.", i);
                    return;
                }
            }
            try {
                values[0] = Long.toString(Long.parseLong(values[0]));
            } catch (NumberFormatException ex) {
                showError("Supplier ID must be a whole number within the BIGINT range.", 0);
                return;
            }
            if (!values[4].isEmpty() && !values[4].matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                showError("Please enter a valid email address, or leave it blank.", 4);
                return;
            }
            save.setEnabled(false);
            boolean saved;
            try {
                saved = saver.save(values);
            } finally {
                save.setEnabled(true);
            }
            if (saved) {
                dispose();
                JOptionPane.showMessageDialog(owner, updating
                    ? "Supplier updated successfully!" : "Supplier added successfully!");
            }
        });
        getRootPane().setDefaultButton(save);
        getContentPane().setPreferredSize(new Dimension(480, 365));
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void showError(String message, int field) {
        JOptionPane.showMessageDialog(this, message, "Invalid Supplier", JOptionPane.ERROR_MESSAGE);
        fields[field].requestFocusInWindow();
        fields[field].selectAll();
    }
}
