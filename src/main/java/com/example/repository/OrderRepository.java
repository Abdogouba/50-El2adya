package com.example.repository;

import com.example.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

import com.example.model.Order;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends MainRepository<Order>{

    @Value("${spring.application.orderDataPath}")
    private String dataPath;

    public OrderRepository() {
    }

    @Override
    protected String getDataPath() {
        return this.dataPath;
    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

    public void addOrder(Order order) {
        this.save(order);
    }

    public Order getOrderById(UUID orderId) {
        Order order = null;

        for (Order o : this.findAll())
            if (o.getId().equals(orderId)) {
                order = o;
                break;
            }

        return order;
    }

    public ArrayList<Order> getOrders() {
        return this.findAll();
    }

    public void deleteOrderById(UUID orderId) {
        ArrayList<Order> orders = this.findAll();

        orders.removeIf(o -> o.getId().equals(orderId));

        this.overrideData(orders);
    }
}