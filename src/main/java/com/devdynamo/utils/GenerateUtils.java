package com.devdynamo.utils;


import java.util.UUID;

public class GenerateUtils {
    public static String generateOrderCode() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
