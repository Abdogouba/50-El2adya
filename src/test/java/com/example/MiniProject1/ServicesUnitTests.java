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


    // Cart Tests



    @Test
    void addCart_withValidInput_shouldReturnSameCart() {
        Cart cart = new Cart(UUID.randomUUID(), new ArrayList<>());

        Cart result = this.cartService.addCart(cart);

        assertEquals(cart.getUserId(), result.getUserId());
        assertTrue(result.getProducts().isEmpty());
        assertNotNull(result.getId());
    }

    @Test
    void addCart_withDuplicateId_shouldThrowException() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());

        this.cartRepository.save(cart);

        assertThrows(ResponseStatusException.class, () -> {this.cartService.addCart(cart);});
    }

    @Test
    void addCart_withNoUser_shouldThrowException() {
        Cart cart = new Cart();
        cart.setProducts(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> {this.cartService.addCart(cart);});
    }

    @Test
    void getCarts_shouldReturnEmptyList() {
        this.cartRepository.overrideData(new ArrayList<>());

        ArrayList<Cart> result = this.cartService.getCarts();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCarts_shouldReturnListOfOneCart() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        ArrayList<Cart> carts = new ArrayList<>();
        carts.add(cart);
        this.cartRepository.overrideData(carts);

        ArrayList<Cart> result = this.cartService.getCarts();

        assertTrue(result.size() == 1);
        assertEquals(cart.getId(), result.get(0).getId());
    }

    @Test
    void getCarts_shouldReturnListOfTwoCarts() {
        Cart cart1 = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        Cart cart2 = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        ArrayList<Cart> carts = new ArrayList<>();
        carts.add(cart1);
        carts.add(cart2);
        this.cartRepository.overrideData(carts);

        ArrayList<Cart> result = this.cartService.getCarts();

        assertTrue(result.size() == 2);
        assertEquals(cart1.getId(), result.get(0).getId());
        assertEquals(cart2.getId(), result.get(1).getId());
    }

    @Test
    void getCartById_validId_shouldReturnCart() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.save(cart);

        Cart result = this.cartService.getCartById(cart.getId());

        assertEquals(cart.getId(), result.getId());
    }

    @Test
    void getCartById_invalidId_shouldThrowException() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.save(cart);

        assertThrows(ResponseStatusException.class, () -> {this.cartService.getCartById(UUID.randomUUID());});
    }

    @Test
    void getCartById_noCarts_shouldThrowException() {
        this.cartRepository.overrideData(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> {this.cartService.getCartById(UUID.randomUUID());});
    }

    @Test
    void getCartByUserId_validUserId_shouldReturnCart() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.save(cart);

        Cart result = this.cartService.getCartByUserId(cart.getUserId());

        assertEquals(cart.getId(), result.getId());
    }

    @Test
    void getCartByUserId_invalidUserId_shouldThrowException() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.save(cart);

        assertThrows(ResponseStatusException.class, () -> {this.cartService.getCartByUserId(UUID.randomUUID());});
    }

    @Test
    void getCartByUserId_noCarts_shouldThrowException() {
        this.cartRepository.overrideData(new ArrayList<>());
        assertThrows(ResponseStatusException.class, () -> {this.cartService.getCartByUserId(UUID.randomUUID());});
    }

    @Test
    void addProductToCart_validInput_shouldAddProductToCart() {
        Product product = new Product("cola", 10.0);
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.overrideData(new ArrayList<>());
        this.cartRepository.save(cart);

        this.cartService.addProductToCart(cart.getId(), product);

        Cart result = this.cartRepository.findAll().get(0);
        assertEquals(cart.getId(), result.getId());
        assertNotNull(result.getProducts().get(0).getId());
        assertEquals(product.getName(), result.getProducts().get(0).getName());
        assertEquals(product.getPrice(), result.getProducts().get(0).getPrice());
    }

    @Test
    void addProductToCart_invalidCartId_shouldThrowException() {
        Product product = new Product("cola", 10.0);

        assertThrows(ResponseStatusException.class, () -> {this.cartService.addProductToCart(UUID.randomUUID(), product);});
    }

    @Test
    void addProductToCart_productWithNegativePrice_shouldThrowException() {
        Product product = new Product("cola", -10.0);
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.save(cart);

        assertThrows(ResponseStatusException.class, () -> {this.cartService.addProductToCart(cart.getId(), product);});
    }

    @Test
    void deleteProductFromCart_validInput_shouldDeleteProductFromCart() {
        Product product = new Product(UUID.randomUUID(),"cola", 10.0);
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        cart.getProducts().add(product);
        this.cartRepository.overrideData(new ArrayList<>());
        this.cartRepository.save(cart);

        this.cartService.deleteProductFromCart(cart.getId(), product);

        Cart result = this.cartRepository.findAll().get(0);
        assertEquals(cart.getId(), result.getId());
        assertTrue(result.getProducts().isEmpty());
    }

    @Test
    void deleteProductFromCart_invalidCartId_shouldThrowException() {
        Product product = new Product(UUID.randomUUID(),"cola", 10.0);

        assertThrows(ResponseStatusException.class, () -> {this.cartService.deleteProductFromCart(UUID.randomUUID(), product);});
    }

    @Test
    void deleteProductFromCart_productWithoutId_shouldThrowException() {
        Product product = new Product("cola", 10.0);
        product.setId(null);
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.save(cart);

        assertThrows(ResponseStatusException.class, () -> {this.cartService.deleteProductFromCart(cart.getId(), product);});
    }

    @Test
    void deleteCartById_invalidId_shouldThrowException() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.save(cart);

        assertThrows(ResponseStatusException.class, () -> {this.cartService.deleteCartById(UUID.randomUUID());});
    }

    @Test
    void deleteCartById_validId_shouldDeleteCart() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.overrideData(new ArrayList<>());
        this.cartRepository.save(cart);

        this.cartService.deleteCartById(cart.getId());

        assertTrue(this.cartRepository.findAll().isEmpty());
    }

    @Test
    void deleteCartById_noCarts_shouldThrowException() {
        this.cartRepository.overrideData(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> {this.cartService.deleteCartById(UUID.randomUUID());});
    }



    // User Tests


    @Test
    void deleteUserById_validId_shouldDeleteUser() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        this.userRepository.overrideData(new ArrayList<>());
        this.userRepository.save(user);

        this.userService.deleteUserById(user.getId());

        assertTrue(this.userRepository.findAll().isEmpty());
    }

    @Test
    void deleteUserById_invalidId_shouldThrowException() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        this.userRepository.save(user);

        assertThrows(ResponseStatusException.class, () -> {this.userService.deleteUserById(UUID.randomUUID());});
    }

    @Test
    void deleteUserById_noUsers_shouldThrowException() {
        this.userRepository.overrideData(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> {this.userService.deleteUserById(UUID.randomUUID());});
    }

    @Test
    void getUsers_shouldReturnEmptyList() {
        this.userRepository.overrideData(new ArrayList<>());

        ArrayList<User> result = this.userService.getUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getUsers_shouldReturnListOfOneUser() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        this.userRepository.overrideData(users);

        ArrayList<User> result = this.userService.getUsers();

        assertTrue(result.size() == 1);
        assertEquals(user.getId(), result.get(0).getId());
    }

    @Test
    void getUsers_shouldReturnListOfTwoUsers() {
        User user1 = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        User user2 = new User(UUID.randomUUID(), "Aly", new ArrayList<>());
        ArrayList<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        this.userRepository.overrideData(users);

        ArrayList<User> result = this.userService.getUsers();

        assertTrue(result.size() == 2);
        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());
    }

    @Test
    void getUserById_validId_shouldReturnUser() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        this.userRepository.save(user);

        User result = this.userService.getUserById(user.getId());

        assertEquals(user.getId(), result.getId());
    }

    @Test
    void getUserById_invalidId_shouldThrowException() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        this.userRepository.save(user);

        assertThrows(ResponseStatusException.class, () -> {this.userService.getUserById(UUID.randomUUID());});
    }

    @Test
    void getUserById_noUsers_shouldThrowException() {
        this.userRepository.overrideData(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> {this.userService.getUserById(UUID.randomUUID());});
    }

    @Test
    void addUser_validInput_shouldReturnSameUser() {
        User user = new User();
        user.setName("Amr");

        User result = this.userService.addUser(user);

        assertNotNull(result.getId());
        assertEquals(user.getName(), result.getName());
        assertTrue(result.getOrders().isEmpty());
    }

    @Test
    void addUser_withDuplicateId_shouldThrowException() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setName("Amr");
        this.userRepository.save(user);

        assertThrows(ResponseStatusException.class, () -> {this.userService.addUser(user);});
    }

    @Test
    void addUser_withNameNull_shouldThrowException() {
        User user = new User();

        assertThrows(ResponseStatusException.class, () -> {this.userService.addUser(user);});
    }

    @Test
    void getOrdersByUserId_validIdAndNoOrders_shouldReturnEmptyList() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        this.userRepository.save(user);

        List<Order> orders = this.userService.getOrdersByUserId(user.getId());

        assertTrue(orders.isEmpty());
    }

    @Test
    void getOrdersByUserId_validId_shouldReturnUserOrders() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        Order order = new Order(UUID.randomUUID(), user.getId(), 100.0, new ArrayList<>());
        Product product = new Product(UUID.randomUUID(), "meat", 100.0);
        order.getProducts().add(product);
        user.getOrders().add(order);
        this.userRepository.save(user);

        List<Order> orders = this.userService.getOrdersByUserId(user.getId());

        assertTrue(orders.size() == 1);
        assertEquals(order.getId(), orders.get(0).getId());
    }

    @Test
    void getOrdersByUserId_invalidId_shouldThrowException() {
        assertThrows(ResponseStatusException.class, () -> {this.userService.getOrdersByUserId(UUID.randomUUID());});
    }

    @Test
    void emptyCart_validUserId_shouldEmptyCart() {
        Product product = new Product(UUID.randomUUID(), "cola", 10.0);
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        cart.getProducts().add(product);
        this.cartRepository.overrideData(new ArrayList<>());
        this.cartRepository.save(cart);

        this.userService.emptyCart(cart.getUserId());

        Cart result = this.cartRepository.findAll().get(0);

        assertTrue(result.getProducts().isEmpty());
    }

    @Test
    void emptyCart_validUserIdAndEmptyCart_shouldDoNothing() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        this.cartRepository.overrideData(new ArrayList<>());
        this.cartRepository.save(cart);

        this.userService.emptyCart(cart.getUserId());

        Cart result = this.cartRepository.findAll().get(0);

        assertTrue(result.getProducts().isEmpty());
    }

    @Test
    void emptyCart_invalidUserId_shouldDoNothing() {
        this.userService.emptyCart(UUID.randomUUID());
    }

    @Test
    void removeOrderFromUser_invalidUserId_shouldThrowException() {
        assertThrows(ResponseStatusException.class, () -> {this.userService.removeOrderFromUser(UUID.randomUUID(), UUID.randomUUID());});
    }

    @Test
    void removeOrderFromUser_validInput_shouldRemoveOrderFromUser() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        Order order = new Order(UUID.randomUUID(), user.getId(), 0, new ArrayList<>());
        user.getOrders().add(order);
        this.userRepository.overrideData(new ArrayList<>());
        this.userRepository.save(user);

        this.userService.removeOrderFromUser(user.getId(), order.getId());

        User result = this.userRepository.findAll().get(0);
        assertEquals(user.getId(), result.getId());
        assertTrue(result.getOrders().isEmpty());
    }

    @Test
    void removeOrderFromUser_invalidOrderId_shouldThrowException() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        Order order = new Order(UUID.randomUUID(), user.getId(), 0, new ArrayList<>());
        user.getOrders().add(order);
        this.userRepository.save(user);

        assertThrows(ResponseStatusException.class, () -> {this.userService.removeOrderFromUser(user.getId(), UUID.randomUUID());});
    }

    @Test
    void addOrderToUser_invalidUserId_shouldThrowException() {
        assertThrows(ResponseStatusException.class, () -> {this.userService.addOrderToUser(UUID.randomUUID());});
    }

    @Test
    void addOrderToUser_noCart_shouldThrowException() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        this.userRepository.save(user);

        assertThrows(ResponseStatusException.class, () -> {this.userService.addOrderToUser(user.getId());});
    }

    @Test
    void addOrderToUser_validInput_shouldAddOrderToUser() {
        User user = new User(UUID.randomUUID(), "Amr", new ArrayList<>());
        this.userRepository.overrideData(new ArrayList<>());
        this.userRepository.save(user);
        Product product = new Product(UUID.randomUUID(), "meat", 300.0);
        Cart cart = new Cart(UUID.randomUUID(), user.getId(), new ArrayList<>());
        cart.getProducts().add(product);
        this.productRepository.save(product);
        this.cartRepository.overrideData(new ArrayList<>());
        this.cartRepository.save(cart);

        this.userService.addOrderToUser(user.getId());

        Cart resultCart = this.cartRepository.findAll().get(0);
        assertTrue(resultCart.getProducts().isEmpty());
        User resultUser = this.userRepository.findAll().get(0);
        assertEquals(user.getId(), resultUser.getId());
        assertTrue(!resultUser.getOrders().isEmpty());
        assertTrue(resultUser.getOrders().size() == 1);
        assertTrue(resultUser.getOrders().get(0).getProducts().get(0).getId().equals(product.getId()));
    }



}
