package com.mobile2.uts_elsid.model;

public class Cart {
    private int productId;
    private String title;
    private String name;
    private String variantName;
    private String image;  // This is the image URL field
    private double price;
    private int quantity;
    private int stock;
    private double discount;

    public Cart(int productId, String title, String image, double price,
                int quantity, int stock, String variantName, double discount) {
        this.productId = productId;
        this.title = title;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
        this.variantName = variantName;
        this.discount = discount;
    }

    // Getters and setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getImage() { return image; }  // This is the getter for image URL
    public void setImage(String image) { this.image = image; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
}