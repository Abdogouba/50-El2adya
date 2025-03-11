package com.example.service;

import com.example.model.Cart;

import com.example.model.Product;
import com.example.model.User;
import com.example.repository.MainRepository;
import com.example.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;


@Service
@SuppressWarnings("rawtypes")
public class CartService extends MainService<Cart> {

    //The Dependency Injection Variables
    //The Constructor with the requried variables mapping the Dependency Injection.

    private final CartRepository cartRepository;
    // private final UserService userService;

    @Autowired
    public CartService(MainRepository<Cart> mainRepository, CartRepository cartRepository) {
        super(mainRepository);
        this.cartRepository = cartRepository;
        //this.userService = userService;
    }

    public Cart addCart(Cart cart) {
        if (cart.getId() == null)
            cart.setId(UUID.randomUUID());
        else if(this.validCart(cart.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart id already exists");
        if (cart.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart must have a user id");
        if (cart.getProducts() == null)
            cart.setProducts(new ArrayList<>());
        return this.cartRepository.addCart(cart);
    }

    public ArrayList<Cart> getCarts() {
        return this.cartRepository.getCarts();
    }

    public Cart getCartById(UUID cartId) {
        Cart cart = this.cartRepository.getCartById(cartId);
        if (cart == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        else
            return cart;
    }

    public boolean validCart(UUID id) {
        Cart cart = this.cartRepository.getCartById(id);
        return cart != null;
    }

    public void deleteCartById(UUID cartId) {
        if (!this.validCart(cartId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        this.cartRepository.deleteCartById(cartId);
    }

    public void addProductToCart(UUID cartId, Product product) {
        if (!this.validCart(cartId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");

        if (product.getId() == null)
            product.setId(UUID.randomUUID());

        if (product.getPrice() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product price cannot be negative");

        if (product.getName() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name cannot be null");

        this.cartRepository.addProductToCart(cartId, product);
    }

    public void emptyCartByUserId(UUID userId) {
        this.cartRepository.emptyCartByUserId(userId);
    }

    public Cart getCartByUserId(UUID userId) {
        Cart cart = this.cartRepository.getCartByUserId(userId);

        if (cart == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        else
            return cart;
    }

    public Cart getCartByUserIdReturnsNull(UUID userId) {
        return this.cartRepository.getCartByUserId(userId);
    }

    public void deleteProductFromCart(UUID cartId, Product product) {
        if (!this.validCart(cartId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");

        if (product.getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id cannot be null");

        Cart cart = this.getCartById(cartId);

        Boolean productFoundInCart = false;

        for (Product p : cart.getProducts())
            if (p.getId().equals(product.getId())) {
                productFoundInCart = true;
                this.cartRepository.deleteProductFromCart(cartId, product);
            }

        if (!productFoundInCart)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in cart");
    }
}