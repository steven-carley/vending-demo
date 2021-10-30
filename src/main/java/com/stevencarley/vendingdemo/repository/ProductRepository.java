package com.stevencarley.vendingdemo.repository;

import com.stevencarley.vendingdemo.config.ProductConfig;
import com.stevencarley.vendingdemo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductRepository {

    private final ProductConfig productConfig;

    @Autowired
    public ProductRepository(ProductConfig productConfig) {
        this.productConfig = productConfig;
    }

    private ConcurrentHashMap<Product, Integer> inventory = new ConcurrentHashMap<>();

    @PostConstruct
    public void initInventory() {
        productConfig.getProducts().stream().forEach(product -> inventory.put(product, productConfig.getDefaultStock()));
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(productConfig.getProducts());
    }

    public Product getProduct(String id) {
        return productConfig.getProducts().stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public Integer getProductCount(String id) {
        var product = getProduct(id);
        if (product == null) {
            return null;
        }
        return inventory.get(product);
    }

    public synchronized boolean buyProduct(String id) {
        var count = getProductCount(id);
        if (count == null || count <= 0) {
            return false;
        }
        inventory.put(getProduct(id), --count);
        return true;
    }

    public synchronized void resetInventory() {
        productConfig.getProducts().stream().forEach(product -> inventory.put(product, productConfig.getDefaultStock()));
    }
}
