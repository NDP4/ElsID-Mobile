package com.mobile2.uts_elsid.api;

import com.mobile2.uts_elsid.model.Product;
import java.util.List;

public class ProductResponse {
    private int id;
    private String name;
    private String title;
    private List<String> images;

    private String image;
    private double price;
    private int status;
    private List<Product> products;
    private Product product;

    public int getStatus() { return status; }
    public List<Product> getProducts() { return products; }
    public Product getProduct() { return product; }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTitle() { return title; }
    public List<String> getImages() { return images; }
    public String getImage() { return image; }
    public double getPrice() { return price; }

    public static class ProductDetail {
        private int status;
        private Product product;

        public int getStatus() { return status; }
        public Product getProduct() { return product; }
    }
}