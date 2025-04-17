package com.mobile2.uts_elsid.api;

import com.mobile2.uts_elsid.model.Product;
import java.util.List;

public class ProductResponse {
    private int status;
    private List<Product> products;
    private Product product;

    public int getStatus() { return status; }
    public List<Product> getProducts() { return products; }
    public Product getProduct() { return product; }

    public static class ProductDetail {
        private int status;
        private Product product;

        public int getStatus() { return status; }
        public Product getProduct() { return product; }
    }
}