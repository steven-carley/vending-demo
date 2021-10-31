package com.stevencarley.vendingdemo;

public final class AppConstants {
    private AppConstants() {
    }

    public static final String DISPLAY_TOPIC = "/topic/display";
    public static final String RETURN_TOPIC = "/topic/return";

    public static final String THANK_YOU_MESSAGE = "THANK YOU";
    public static final String SOLD_OUT_MESSAGE = "SOLD OUT";
    public static final String EXACT_CHANGE_MESSAGE = "EXACT CHANGE ONLY";
    public static final String DEFAULT_MESSAGE = "INSERT COIN";
    public static final String PRICE_MESSAGE_PREFIX = "PRICE ";
}
