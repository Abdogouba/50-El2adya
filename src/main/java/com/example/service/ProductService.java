package com.example.service;

import com.example.model.Product;
import com.example.model.User;
import com.example.repository.MainRepository;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class ProductService extends MainService<Product> {

    //The Dependency Injection Variables
    //The Constructor with the requried variables mapping the Dependency Injection.

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(MainRepository<Product> mainRepository, ProductRepository productRepository) {
        super(mainRepository);
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        if (product.getPrice() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product price cannot be negative");

        if (product.getName() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name cannot be null");

        if (product.getId() == null)
            product.setId(UUID.randomUUID());

        if (!this.validProduct(product.getId()))
            return this.productRepository.addProduct(product);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id already exists");
    }

    public ArrayList<Product> getProducts() {
        return this.productRepository.getProducts();
    }

    public Product getProductById(UUID productId) {
        Product product = this.productRepository.getProductById(productId);
        if (product == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        else
            return product;
    }

    public boolean validProduct(UUID productId) {
        Product product = this.productRepository.getProductById(productId);
        return product != null;
    }

    public void deleteProductById(UUID productId) {
        if (!this.validProduct(productId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        this.productRepository.deleteProductById(productId);
    }

    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
        for (UUID productId : productIds)
            if (!this.validProduct(productId))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One of the Products or more were not found");

        if (discount > 0 && discount <= 100.0)
            this.productRepository.applyDiscount(discount, productIds);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "discount must be greater than 0 and less than or equal 100");
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        if (!this.validProduct(productId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        if (newPrice < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product price cannot be negative");
        return this.productRepository.updateProduct(productId, newName, newPrice);
    }
}