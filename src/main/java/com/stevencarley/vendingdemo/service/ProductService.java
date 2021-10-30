package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.DispenseProductEvent;
import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.model.Product;
import com.stevencarley.vendingdemo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.stevencarley.vendingdemo.AppConstants.SOLD_OUT_MESSAGE;
import static com.stevencarley.vendingdemo.AppConstants.THANK_YOU_MESSAGE;

@Service
public class ProductService {

    private final TransactionService transactionService;
    private final MessageFormatterService messageFormatterService;
    private final VendingEventPublisher eventPublisher;
    private final ChangeService changeService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(TransactionService transactionService,
                          MessageFormatterService messageFormatterService,
                          VendingEventPublisher eventPublisher,
                          ChangeService changeService,
                          ProductRepository productRepository) {
        this.transactionService = transactionService;
        this.messageFormatterService = messageFormatterService;
        this.eventPublisher = eventPublisher;
        this.changeService = changeService;
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.getProducts();
    }

    public boolean purchaseProduct(String productId) {
        var product = productRepository.getProduct(productId);
        if (product != null) {
            var productCount = productRepository.getProductCount(product.getId());
            if (productCount == null || productCount <= 0) {
                publishSoldOutMessages();
                return false;
            }
            var totalAmount = transactionService.getTotalCurrencies();
            if (product.getPrice().compareTo(totalAmount) <= 0) {
                return buyProduct(product, totalAmount);
            }
            else {
                eventPublisher.publishEvent(new UpdateDisplayEvent(this, messageFormatterService.formatPriceMessage(product.getPrice())));
                eventPublisher.publishEventAfterDelay(new UpdateDisplayEvent(this, totalAmount));
                return false;
            }
        }
        else {
            eventPublisher.publishEvent(new UpdateDisplayEvent(this, transactionService.getTotalCurrencies()));
            return false;
        }
    }

    public void resetInventory() {
        productRepository.resetInventory();
    }

    private boolean buyProduct(Product product, BigDecimal totalAmount) {
        if (productRepository.buyProduct(product.getId())) {
            eventPublisher.publishEvent(new DispenseProductEvent(this, product));
            changeService.makeAndDispenseChange(product.getPrice(), totalAmount);
            eventPublisher.publishEvent(new UpdateDisplayEvent(this, THANK_YOU_MESSAGE));
            eventPublisher.publishEventAfterDelay(new UpdateDisplayEvent(this, transactionService.getTotalCurrencies()));
            return true;
        }
        else {
            publishSoldOutMessages();
            return false;
        }
    }

    private void publishSoldOutMessages() {
        eventPublisher.publishEvent(new UpdateDisplayEvent(this, SOLD_OUT_MESSAGE));
        eventPublisher.publishEventAfterDelay(new UpdateDisplayEvent(this, transactionService.getTotalCurrencies()));
    }
}
