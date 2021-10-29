package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.DispenseProductEvent;
import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.stevencarley.vendingdemo.AppConstants.THANK_YOU_MESSAGE;

@Service
public class ProductsService {

    private final TransactionService transactionService;
    private final MessageFormatterService messageFormatterService;
    private final VendingEventPublisher eventPublisher;

    private final List<Product> products;

    @Autowired
    public ProductsService(TransactionService transactionService,
                           MessageFormatterService messageFormatterService,
                           VendingEventPublisher eventPublisher) {
        this.transactionService = transactionService;
        this.messageFormatterService = messageFormatterService;
        this.eventPublisher = eventPublisher;

        products = List.of(Product.builder().id("1").description("cola").price(new BigDecimal("1.00")).build(),
                Product.builder().id("2").description("chips").price(new BigDecimal("0.50")).build(),
                Product.builder().id("3").description("candy").price(new BigDecimal("0.65")).build());
    }

    public List<Product> getProducts() {
        return products;
    }

    public boolean purchaseProduct(String productId) {
        var optional = products.stream().filter(p -> p.getId().equals(productId)).findFirst();
        if (optional.isPresent()) {
            var totalAmount = transactionService.getTotalCurrencies();
            if (optional.get().getPrice().compareTo(totalAmount) <= 0) {
                eventPublisher.publishEvent(new DispenseProductEvent(this, optional.get()));
                //TODO: Give change instead of clearing amount
                transactionService.clearCurrencies();
                eventPublisher.publishEvent(new UpdateDisplayEvent(this, THANK_YOU_MESSAGE));
                eventPublisher.publishEventAfterDelay(new UpdateDisplayEvent(this, transactionService.getTotalCurrencies()));
                return true;
            }
            else {
                eventPublisher.publishEvent(new UpdateDisplayEvent(this, messageFormatterService.formatPriceMessage(optional.get().getPrice())));
                eventPublisher.publishEventAfterDelay(new UpdateDisplayEvent(this, totalAmount));
                return false;
            }
        }
        else {
            eventPublisher.publishEvent(new UpdateDisplayEvent(this, transactionService.getTotalCurrencies()));
            return false;
        }
    }
}
