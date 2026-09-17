package com.example.demo;

import org.springframework.stereotype.Component;

@Component
public class CommonUtil {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNull(Object value) {
        return value == null;
    }

    public static boolean isValidDouble(double value) {
        return value == 0;
    }
}
