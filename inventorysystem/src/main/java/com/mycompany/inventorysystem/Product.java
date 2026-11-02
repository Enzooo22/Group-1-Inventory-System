package com.mycompany.inventorysystem;

public class Product {
    private final long productId;
    private String productName;
    private String category;
    private int quantity;
    private Long supplierId;
    private double price;

    public Product(long productId, String productName, String category,
                   int quantity, Long supplierId, double price) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.price = price;
    }

    public long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
