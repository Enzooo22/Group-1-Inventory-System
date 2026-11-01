package com.mycompany.inventorysystem;

import javax.swing.JFrame;

public class UpdateProductDialog extends AddProductDialog {

    public UpdateProductDialog(JFrame owner, ProductSaver saver, String[] product) {
        super(owner, saver, product);
    }
}
