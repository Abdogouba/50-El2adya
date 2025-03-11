package com.example.repository;

import com.example.model.Product;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

import com.example.model.Cart;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart>{

    @Value("${spring.application.cartDataPath}")
    private String dataPath;

    public CartRepository() {
    }

    @Override
    protected String getDataPath() {
        return this.dataPath;
    }

    @Override
    protected Class<Cart[]> getArrayType() {
        return Cart[].class;
    }

    public Cart addCart(Cart cart) {
        this.save(cart);
        return cart;
    }

    public ArrayList<Cart> getCarts() {
        return this.findAll();
    }

    public Cart getCartById(UUID cartId) {
        Cart cart = null;

        for (Cart c : this.findAll())
            if (c.getId().equals(cartId)) {
                cart = c;
                break;
            }

        return cart;
    }

    public void deleteCartById(UUID cartId) {
        ArrayList<Cart> carts = this.findAll();

        carts.removeIf(c -> c.getId().equals(cartId));

        this.overrideData(carts);
    }

    public void addProductToCart(UUID cartId, Product product) {
        ArrayList<Cart> carts = this.findAll();

        for (int i = 0; i < carts.size(); i++) {
            Cart cart = carts.get(i);
            if (cart.getId().equals(cartId)) {
                cart.getProducts().add(product);
                break;
            }
        }

        this.overrideData(carts);
    }

    public void emptyCartByUserId(UUID userId) {
        ArrayList<Cart> carts = this.findAll();

        for (int i = 0; i < carts.size(); i++) {
            Cart cart = carts.get(i);
            if (cart.getUserId().equals(userId)) {
                cart.getProducts().clear();
                break;
            }
        }

        this.overrideData(carts);
    }

    public Cart getCartByUserId(UUID userId) {
        Cart cart = null;

        for (Cart c : this.findAll())
            if (c.getUserId().equals(userId)) {
                cart = c;
                break;
            }

        return cart;
    }

    public void deleteProductFromCart(UUID cartId, Product product) {
        ArrayList<Cart> carts = this.findAll();

        for (int i = 0; i < carts.size(); i++) {
            Cart cart = carts.get(i);
            if (cart.getId().equals(cartId)) {
                cart.getProducts().removeIf(p -> p.getId().equals(product.getId()));
                break;
            }
        }

        this.overrideData(carts);
    }
}