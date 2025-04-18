package com.mobile2.uts_elsid.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    private static final NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public static String format(double amount) {
        return format.format(amount);
    }
}