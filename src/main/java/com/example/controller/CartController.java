package com.example.controller;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {
    //The Dependency Injection Variables
    //The Constructor with the requried variables mapping the Dependency Injection.

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/")
    public Cart addCart(@RequestBody Cart cart) {
        return this.cartService.addCart(cart);
    }

    @GetMapping("/")
    public ArrayList<Cart> getCarts() {
        return this.cartService.getCarts();
    }

    @GetMapping("/{cartId}")
    public Cart getCartById(@PathVariable UUID cartId) {
        return this.cartService.getCartById(cartId);
    }

    @DeleteMapping("/delete/{cartId}")
    public String deleteCartById(@PathVariable UUID cartId) {
        if (this.cartService.validCart(cartId)) {
            this.cartService.deleteCartById(cartId);
            return "Cart deleted successfully";
        }

        return "Cart not found";
    }

    @PutMapping("/addProduct/{cartId}")
    public String addProductToCart(@PathVariable UUID cartId, @RequestBody Product product) {
        if (this.cartService.validCart(cartId)) {
            this.cartService.addProductToCart(cartId, product);
            return "Product added successfully";
        }

        return "Cart not found";
    }
}

