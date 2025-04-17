package com.mobile2.uts_elsid.model;

import android.util.Log;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
//    private int id;
    private String id;
    private String title;
    private String description;
    private String category;
    private double price;
    private double discount;
    private int main_stock;
    private String status;
    private boolean has_variants;
    private int hasVariants;
    private String created_at;
    private List<String> images;
    private List<ProductVariant> variants;
    private boolean available;

    // Getters
//    public int getId() {
//        Log.d("Product", "getId: " + id);
//        return id;
//    }
    public int getId() {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            Log.e("Product", "Error parsing ID: " + id);
            return -1;
        }
    }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public double getDiscount() { return discount; }
    public int getMainStock() { return main_stock; }
    public String getStatus() { return status; }
    public boolean isHasVariants() { return has_variants; }
    public int getHasVariants() { return hasVariants; }
    public String getCreatedAt() { return created_at; }
    public List<String> getImages() { return images; }
    public List<ProductVariant> getVariants() { return variants; }
    public boolean isAvailable() { return available; }

//    public void setId(int id) {
//        Log.d("Product", "setId: " + id);
//        this.id = id;
//    }
    public void setId(String id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setDiscount(double discount) {
        this.discount = discount;
    }
    public void setMainStock(int main_stock) {
        this.main_stock = main_stock;
    }
    public void setHasVariants(boolean has_variants) {
        this.has_variants = has_variants;
    }
    public void setHasVariants(int hasVariants) {
        this.hasVariants = hasVariants;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}