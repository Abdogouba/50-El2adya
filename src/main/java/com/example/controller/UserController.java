package com.example.controller;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.CartService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    //The Dependency Injection Variables
    //The Constructor with the requried variables mapping the Dependency Injection.

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ArrayList<User> getUsers() {
        return this.userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable UUID userId) {
        return this.userService.getUserById(userId);
    }

    @PostMapping("/")
    public User addUser(@RequestBody User user) {
        return this.userService.addUser(user);
    }

    @DeleteMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable UUID userId) {
        if (this.userService.validUser(userId)) {
            this.userService.deleteUserById(userId);
            return "User deleted successfully";
        }

        return "User not found";
    }

    @DeleteMapping("/{userId}/emptyCart")
    public String emptyCart(@PathVariable UUID userId) {
        this.userService.emptyCart(userId);
        return "Cart emptied successfully";
    }

    @GetMapping("/{userId}/orders")
    public List<Order> getOrdersByUserId(@PathVariable UUID userId) {
        return this.userService.getOrdersByUserId(userId);
    }

    @PostMapping("/{userId}/removeOrder")
    public String removeOrderFromUser(@PathVariable UUID userId, @RequestParam UUID orderId) {
        if (this.userService.validUser(userId)) {
            this.userService.removeOrderFromUser(userId, orderId);
            return "Order removed successfully";
        }

        return "User not found";
    }

    @PostMapping("/{userId}/checkout")
    public String addOrderToUser(@PathVariable UUID userId) {
        if (this.userService.validUser(userId)) {
            this.userService.addOrderToUser(userId);
            return "Order added successfully";
        }

        return "User not found";
    }

    @PutMapping("/addProductToCart")
    public String addProductToCart(@RequestParam UUID userId, @RequestParam UUID productId) {
        this.userService.addProductToCart(userId, productId);
        return "Product added to cart";
    }

    @PutMapping("/deleteProductFromCart")
    public String deleteProductFromCart(@RequestParam UUID userId, @RequestParam UUID productId) {
        return this.userService.deleteProductFromCart(userId, productId);
    }
}