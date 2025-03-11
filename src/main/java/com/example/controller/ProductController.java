package com.example.controller;

import com.example.model.Product;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {
    //The Dependency Injection Variables
    //The Constructor with the requried variables mapping the Dependency Injection.

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public Product addProduct(@RequestBody Product product) {
        return this.productService.addProduct(product);
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts() {
        return this.productService.getProducts();
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable UUID productId) {
        return this.productService.getProductById(productId);
    }

    @DeleteMapping("/delete/{productId}")
    public String deleteProductById(@PathVariable UUID productId) {
        if (this.productService.validProduct(productId)) {
            this.productService.deleteProductById(productId);
            return "Product deleted successfully";
        }

        return "Product not found";
    }

    @PutMapping("/applyDiscount")
    public String applyDiscount(@RequestParam double discount,@RequestBody ArrayList<UUID>
            productIds) {
        this.productService.applyDiscount(discount, productIds);
        return "Discount applied successfully";
    }

    @PutMapping("/update/{productId}")
    public Product updateProduct(@PathVariable UUID productId,
                                 @RequestBody Map<String,Object> body) {
        String newName = (String) body.get("newName");
        Double newPrice = (Double) body.get("newPrice");
        return this.productService.updateProduct(productId, newName, newPrice);
    }
}
