package com.stevencarley.vendingdemo.config;

import com.stevencarley.vendingdemo.model.Product;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "product")
@Data
public class ProductConfig {

    private List<Product> products;
    private int defaultStock;
}
