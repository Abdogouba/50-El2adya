package com.example.repository;

import com.example.model.Product;
import com.example.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends MainRepository<Product>{

    public ProductRepository() {
    }

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/products.json";
    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }

    public Product addProduct(Product product) {
        this.save(product);
        return product;
    }

    public ArrayList<Product> getProducts() {
        return this.findAll();
    }

    public Product getProductById(UUID productId) {
        Product product = null;

        for (Product p : this.findAll())
            if (p.getId().equals(productId)) {
                product = p;
                break;
            }

        return product;
    }

    public void deleteProductById(UUID productId) {
        ArrayList<Product> products = this.findAll();

        products.removeIf(u -> u.getId().equals(productId));

        this.overrideData(products);
    }

    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
        double discountPercent = discount / 100.0;

        ArrayList<Product> products = this.findAll();

        for (int i = 0; i < products.size(); i++)
            if (productIds.contains(products.get(i).getId())) {
                Product p = products.get(i);
                p.setPrice(p.getPrice() - (p.getPrice() * discountPercent));
            }

        this.overrideData(products);
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        ArrayList<Product> products = this.findAll();
        Product product = null;

        for (int i = 0; i < products.size(); i++)
            if (products.get(i).getId().equals(productId)) {
                if (newName != null)
                    products.get(i).setName(newName);
                products.get(i).setPrice(newPrice);
                product = products.get(i);
                break;
            }

        this.overrideData(products);

        return product;
    }
}


