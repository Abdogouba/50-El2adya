package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ServicesUnitTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    // Product tests

    @Test
    void addProduct_withValidInput_shouldReturnSameProduct() {
        Product product = new Product();
        product.setPrice(10);
        product.setName("Cola");

        Product result = this.productService.addProduct(product);

        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getName(), result.getName());
        assertNotNull(result.getId());
    }

    @Test
    void addProduct_withDuplicateId_shouldThrowException() {
        UUID id = UUID.randomUUID();

        Product product = new Product(id, "coffee", 15);

        this.productService.addProduct(new Product(id,"cola", 10));

        assertThrows(ResponseStatusException.class, () -> this.productService.addProduct(product));
    }

    @Test
    void addProduct_withNegativePrice_shouldThrowException() {
        Product product = new Product("coffee", -15);

        assertThrows(ResponseStatusException.class, () -> this.productService.addProduct(product));
    }

    @Test
    void getProducts_shouldReturnEmptyList() {
        ArrayList<Product> products = new ArrayList<>();

        this.productRepository.overrideData(products);

        ArrayList<Product> result = this.productService.getProducts();

        assertTrue(result.isEmpty());
    }

    @Test
    void getProducts_shouldReturnListOfOneProduct() {
        ArrayList<Product> products = new ArrayList<>();

        UUID id = UUID.randomUUID();

        products.add(new Product(id,"coffee", 10));

        this.productRepository.overrideData(products);

        ArrayList<Product> result = this.productService.getProducts();

        assertTrue(result.size() == 1);
        assertEquals(result.get(0).getId(), id);
    }

    @Test
    void getProducts_shouldReturnListOfTwoProducts() {
        ArrayList<Product> products = new ArrayList<>();

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        products.add(new Product(id1,"coffee", 10));
        products.add(new Product(id2,"cola", 20));

        this.productRepository.overrideData(products);

        ArrayList<Product> result = this.productService.getProducts();

        assertTrue(result.size() == 2);
        assertEquals(result.get(0).getId(), id1);
        assertEquals(result.get(1).getId(), id2);
    }

    @Test
    void getProductById_validId_shouldReturnProduct() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id,"coffee", 10);
        this.productRepository.save(product);

        Product result = this.productService.getProductById(id);

        assertEquals(result.getId(), product.getId());
        assertEquals(result.getName(), product.getName());
        assertEquals(result.getPrice(), product.getPrice());
    }

    @Test
    void getProductById_noProducts_shouldThrowException() {
        this.productRepository.overrideData(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> this.productService.getProductById(UUID.randomUUID()));
    }

    @Test
    void getProductById_invalidId_shouldThrowException() {
        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product(UUID.randomUUID(),"coffee", 10));
        this.productRepository.overrideData(products);

        UUID id = UUID.randomUUID();

        assertThrows(ResponseStatusException.class, () -> this.productService.getProductById(id));
    }

    @Test
    void updateProduct_invalidId_shouldThrowException() {
        UUID id = UUID.randomUUID();

        assertThrows(ResponseStatusException.class, () -> this.productService.updateProduct(id, "coffee", 10));
    }

    @Test
    void updateProduct_negativePrice_shouldThrowException() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id,"coffee", 10);
        this.productRepository.save(product);

        assertThrows(ResponseStatusException.class, () -> this.productService.updateProduct(id, "coffee", -10));
    }

    @Test
    void updateProduct_validInput_shouldReturnUpdatedProduct() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id,"coffee", 10);
        this.productRepository.save(product);
        String newName = "american coffee";
        Double newPrice = 15.0;

        Product result = this.productService.updateProduct(id, newName, newPrice);

        assertEquals(id, result.getId());
        assertEquals(newName, result.getName());
        assertEquals(newPrice, result.getPrice());
    }

    @Test
    void deleteProductById_invalidId_shouldThrowException() {
        UUID id = UUID.randomUUID();

        assertThrows(ResponseStatusException.class, () -> this.productService.deleteProductById(id));
    }

    @Test
    void deleteProductById_noProducts_shouldThrowException() {
        UUID id = UUID.randomUUID();
        this.productRepository.overrideData(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> this.productService.deleteProductById(id));
    }

    @Test
    void deleteProductById_validId_shouldDeleteProduct() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id,"coffee", 10);
        this.productRepository.save(product);

        this.productService.deleteProductById(id);

        Boolean found = false;
        for (Product p : this.productRepository.findAll())
            if (p.getId().equals(id)) {
                found = true;
                break;
            }

        assertFalse(found);
    }

    @Test
    void applyDiscount_invalidDiscount_shouldThrowException() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id,"coffee", 10);
        this.productRepository.save(product);
        double discount = 0;
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(id);

        assertThrows(ResponseStatusException.class, () -> {this.productService.applyDiscount(discount, productIds);});

    }

    @Test
    void applyDiscount_invalidProductId_shouldThrowException() {
        UUID id = UUID.randomUUID();
        double discount = 10;
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(id);

        assertThrows(ResponseStatusException.class, () -> {this.productService.applyDiscount(discount, productIds);});

    }

    @Test
    void applyDiscount_validInput_shouldApplyDiscount() {
        UUID id1 = UUID.randomUUID();
        Product product1 = new Product(id1,"coffee", 10);
        UUID id2 = UUID.randomUUID();
        Product product2 = new Product(id2,"cola", 5);
        ArrayList<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        this.productRepository.overrideData(products);
        double discount = 10;
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(id1);
        productIds.add(id2);

        this.productService.applyDiscount(discount, productIds);

        products = this.productRepository.findAll();
        assertEquals(productIds.get(0), products.get(0).getId());
        assertEquals(productIds.get(1), products.get(1).getId());
        assertEquals(9.0, products.get(0).getPrice());
        assertEquals(4.5, products.get(1).getPrice());
    }


    // Order Tests


    @Test
    void addOrder_withValidInput_shouldBeAdded() {
        Order order = new Order();
        order.setUserId(UUID.randomUUID());
        order.setTotalPrice(10.0);
        order.setProducts(new ArrayList<>());
        this.orderRepository.overrideData(new ArrayList<>());

        this.orderService.addOrder(order);

        ArrayList<Order> orders = this.orderRepository.findAll();
        assertNotNull(orders.get(0).getId());
        assertEquals(order.getUserId(), orders.get(0).getUserId());
        assertEquals(order.getTotalPrice(), orders.get(0).getTotalPrice());
        assertEquals(order.getProducts().size(), orders.get(0).getProducts().size());
    }

    @Test
    void addOrder_withDuplicateId_shouldThrowException() {
        UUID id = UUID.randomUUID();
        Order order1 = new Order(id, UUID.randomUUID(), 10, new ArrayList<>());
        Order order2 = new Order(id, UUID.randomUUID(), 15, new ArrayList<>());
        this.orderRepository.save(order1);

        assertThrows(ResponseStatusException.class, () -> {this.orderService.addOrder(order2);});
    }

    @Test
    void addOrder_withNegativePrice_shouldThrowException() {
        Order order = new Order(UUID.randomUUID(), -10, new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> {this.orderService.addOrder(order);});
    }

    @Test
    void getOrders_shouldReturnEmptyList() {
        ArrayList<Order> orders = new ArrayList<>();

        this.orderRepository.overrideData(orders);

        ArrayList<Order> result = this.orderService.getOrders();

        assertTrue(result.isEmpty());
    }

    @Test
    void getOrders_shouldReturnListOfOneOrder() {
        ArrayList<Order> orders = new ArrayList<>();

        UUID id = UUID.randomUUID();

        orders.add(new Order(id, UUID.randomUUID(), 10, new ArrayList<>()));

        this.orderRepository.overrideData(orders);

        ArrayList<Order> result = this.orderService.getOrders();

        assertTrue(result.size() == 1);
        assertEquals(id, result.get(0).getId());
    }

    @Test
    void getOrders_shouldReturnListOfTwoOrders() {
        ArrayList<Order> orders = new ArrayList<>();

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        orders.add(new Order(id1, UUID.randomUUID(), 15, new ArrayList<>()));
        orders.add(new Order(id2, UUID.randomUUID(), 10, new ArrayList<>()));

        this.orderRepository.overrideData(orders);

        ArrayList<Order> result = this.orderService.getOrders();

        assertTrue(result.size() == 2);
        assertEquals(id1, result.get(0).getId());
        assertEquals(id2, result.get(1).getId());
    }

    @Test
    void getOrderById_validId_shouldReturnOrder() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Order order = new Order(id, userId, 10, new ArrayList<>());
        this.orderRepository.save(order);

        Order result = this.orderService.getOrderById(id);

        assertEquals(id, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(order.getTotalPrice(), result.getTotalPrice());
        assertEquals(order.getProducts().size(), result.getProducts().size());
    }

    @Test
    void getOrderById_invalidId_shouldThrowException() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), 10, new ArrayList<>());
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order);
        this.orderRepository.overrideData(orders);

        assertThrows(ResponseStatusException.class, () -> {this.orderService.getOrderById(UUID.randomUUID());});
    }

    @Test
    void getOrderById_noOrders_shouldThrowException() {
        this.orderRepository.overrideData(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> {this.orderService.getOrderById(UUID.randomUUID());});
    }

    @Test
    void deleteOrderById_invalidId_shouldThrowException() {
        UUID id = UUID.randomUUID();

        assertThrows(ResponseStatusException.class, () -> {this.orderService.deleteOrderById(id);});
    }

    @Test
    void deleteOrderById_noOrders_shouldThrowException() {
        UUID id = UUID.randomUUID();
        this.orderRepository.overrideData(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> {this.orderService.deleteOrderById(id);});
    }

    @Test
    void deleteOrderById_validId_shouldBeDeleted() {
        UUID id = UUID.randomUUID();
        Order order = new Order(id, UUID.randomUUID(), 10, new ArrayList<>());
        this.orderRepository.overrideData(new ArrayList<>());
        this.orderRepository.save(order);

        this.orderService.deleteOrderById(id);

        assertTrue(this.orderRepository.findAll().isEmpty());
    }





}
