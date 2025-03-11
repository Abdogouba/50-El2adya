package com.example.controller;

import com.example.model.Order;
import com.example.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {
    //The Dependency Injection Variables
    //The Constructor with the requried variables mapping the Dependency Injection.

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/")
    public void addOrder(@RequestBody Order order) {
        this.orderService.addOrder(order);
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable UUID orderId) {
        return this.orderService.getOrderById(orderId);
    }

    @GetMapping("/")
    public ArrayList<Order> getOrders() {
        return this.orderService.getOrders();
    }

    @DeleteMapping("/delete/{orderId}")
    public String deleteOrderById(@PathVariable UUID orderId) {
        if (this.orderService.validOrder(orderId)) {
            this.orderService.deleteOrderById(orderId);
            return "Order deleted successfully";
        }

        return "Order not found";
    }
}

