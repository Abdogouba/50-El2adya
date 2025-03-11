package com.example.service;

import com.example.model.Order;

import com.example.model.User;
import com.example.repository.MainRepository;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;


@Service
@SuppressWarnings("rawtypes")
public class OrderService extends MainService<Order> {

    //The Dependency Injection Variables
    //The Constructor with the requried variables mapping the Dependency Injection.

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(MainRepository<Order> mainRepository, OrderRepository orderRepository) {
        super(mainRepository);
        this.orderRepository = orderRepository;
    }

    public void addOrder(Order order) {
        if (order.getId() == null)
            order.setId(UUID.randomUUID());
        else if (this.validOrder(order.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order id already exists");

        if (order.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must have a user");

        if (order.getTotalPrice() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order total price cannot be negative");

        if (order.getProducts() == null)
            order.setProducts(new ArrayList<>());

        this.orderRepository.addOrder(order);
    }

    public Order getOrderById(UUID orderId) {
        Order order = this.orderRepository.getOrderById(orderId);
        if (order == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        else
            return order;
    }

    public ArrayList<Order> getOrders() {
        return this.orderRepository.getOrders();
    }

    public boolean validOrder(UUID orderId) {
        Order order = this.orderRepository.getOrderById(orderId);
        return order != null;
    }

    public void deleteOrderById(UUID orderId) throws IllegalArgumentException {
        if (!this.validOrder(orderId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        this.orderRepository.deleteOrderById(orderId);
    }
}