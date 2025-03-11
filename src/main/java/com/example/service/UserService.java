package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.MainRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class UserService extends MainService<User> {

    //The Dependency Injection Variables
    //The Constructor with the requried variables mapping the Dependency Injection.

    private final UserRepository userRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public UserService(MainRepository<User> mainRepository, UserRepository userRepository, CartService cartService, ProductService productService, OrderService orderService) {
        super(mainRepository);
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.orderService = orderService;
    }

    public ArrayList<User> getUsers() {
        return this.userRepository.getUsers();
    }

    public User getUserById(UUID userId) {
        User user = this.userRepository.getUserById(userId);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        else
            return user;
    }

    public User addUser(User user) {
        if (user.getId() == null)
            user.setId(UUID.randomUUID());
        else if (validUser(user.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id already exists");

        if (user.getName() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name is required");

        if (user.getOrders() == null)
            user.setOrders(new ArrayList<>());

        return this.userRepository.addUser(user);
    }

    public void deleteUserById(UUID userId) {
        if (!this.validUser(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        this.userRepository.deleteUserById(userId);
    }

    public boolean validUser(UUID id) {
        User user = this.userRepository.getUserById(id);
        return user != null;
    }

    public void emptyCart(UUID userId) {
        this.cartService.emptyCartByUserId(userId);
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        if (this.validUser(userId))
            return this.userRepository.getOrdersByUserId(userId);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        if (!this.validUser(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        User user = this.getUserById(userId);

        Boolean isOrderFound = false;

        for (Order order : user.getOrders())
            if (order.getId().equals(orderId)) {
                isOrderFound = true;
                this.userRepository.removeOrderFromUser(userId, orderId);
                break;
            }

        if (!isOrderFound)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not have this order");
    }

    public void addOrderToUser(UUID userId) {
        if (!this.validUser(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Cart cart = null;
        try {
            cart = this.cartService.getCartByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart is empty");
        }

        if (cart.getProducts().size() == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart is empty");
        else {
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setUserId(userId);
            order.setTotalPrice(calculateCartTotalPrice(cart));
            order.setProducts(new ArrayList<>(cart.getProducts()));

            this.userRepository.addOrderToUser(userId, order);

            this.orderService.addOrder(order);

            this.emptyCart(userId);
        }
    }

    private static double calculateCartTotalPrice(Cart cart) {
        double totalPrice = 0;

        for (Product product : cart.getProducts())
            totalPrice += product.getPrice();

        return totalPrice;
    }

    public void addProductToCart(UUID userId, UUID productId) {
        if (!this.validUser(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        else if (!this.productService.validProduct(productId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        else {
            Cart cart = this.cartService.getCartByUserIdReturnsNull(userId);
            if (cart == null) {
                cart = new Cart();
                cart.setUserId(userId);
                cart = this.cartService.addCart(cart);
            }
            Product product = this.productService.getProductById(productId);
            this.cartService.addProductToCart(cart.getId(), product);
        }
    }

    public String deleteProductFromCart(UUID userId, UUID productId) {
        if (!this.validUser(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        else if (!this.productService.validProduct(productId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        else {
            Cart cart = this.cartService.getCartByUserIdReturnsNull(userId);
            if (cart == null || cart.getProducts().isEmpty())
                return "Cart is empty";
            Product product = this.productService.getProductById(productId);
            this.cartService.deleteProductFromCart(cart.getId(), product);
            return "Product deleted from cart";
        }
    }
}