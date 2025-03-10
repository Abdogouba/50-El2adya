package com.example.repository;

import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User>{

    public UserRepository() {
    }

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/users.json";
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    public ArrayList<User> getUsers() {
        return this.findAll();
    }

    public User getUserById(UUID userId) {
        User user = null;

        for (User u : this.findAll())
            if (u.getId().equals(userId)) {
                user = u;
                break;
            }

        return user;
    }

    public User addUser(User user) {
        this.save(user);
        return user;
    }

    public void deleteUserById(UUID userId) {
        ArrayList<User> users = this.findAll();

        users.removeIf(u -> u.getId().equals(userId));

        this.overrideData(users);
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return this.getUserById(userId).getOrders();
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        ArrayList<User> users = this.findAll();

        for (int i = 0; i < users.size(); i++)
            if (users.get(i).getId().equals(userId)) {
                users.get(i).getOrders().removeIf(o -> o.getId().equals(orderId));
                break;
            }

        this.overrideData(users);
    }

    public void addOrderToUser(UUID userId, Order order) {
        ArrayList<User> users = this.findAll();

        for (int i = 0; i < users.size(); i++)
            if (users.get(i).getId().equals(userId)) {
                users.get(i).getOrders().add(order);
                break;
            }

        this.overrideData(users);
    }
}

