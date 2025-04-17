package com.mobile2.uts_elsid.model;

public class Cart {
    private int productId;
    private String name;
    private String image;
    private double price;
    private int quantity;
    private int stock;

    public Cart(int productId, String name, String image, double price, int quantity, int stock) {
        this.productId = productId;
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
    }

    // Getters and setters
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getStock() { return stock; }
}