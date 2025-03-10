package com.example.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Cart {
    private UUID id;
    private UUID userId;
    private List<Product> products=new ArrayList<>();

    public Cart(UUID id, UUID userId, List<Product> products) {
        this.userId = userId;
        this.id = id;
        this.products = products;
    }

    public Cart(UUID userId, List<Product> products) {
        this.id = UUID.randomUUID();
        this.products = products;
        this.userId = userId;
    }

    public Cart() {
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", products=" + products +
                '}';
    }
}

