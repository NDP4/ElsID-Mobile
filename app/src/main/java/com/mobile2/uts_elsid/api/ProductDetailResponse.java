package com.mobile2.uts_elsid.api;

import com.mobile2.uts_elsid.model.Product;
import java.util.List;

public class ProductDetailResponse {
    private int status;
    private Product product;  // Change from List<Product> to single Product

    public int getStatus() {
        return status;
    }

    public Product getProduct() {
        return product;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}