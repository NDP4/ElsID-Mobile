// CartManager.java
package com.mobile2.uts_elsid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile2.uts_elsid.model.Cart;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREF_NAME = "CartPrefs";
    private static final String KEY_CART = "cart";
    private final SharedPreferences pref;
    private final Gson gson;

    private SharedPreferences sharedPreferences;

    public CartManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<Cart> getCart() {
        String cartJson = pref.getString(KEY_CART, null);
        if (cartJson == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Cart>>(){}.getType();
        return gson.fromJson(cartJson, type);
    }

    public void saveCart(List<Cart> cart) {
        SharedPreferences.Editor editor = pref.edit();
        String cartJson = gson.toJson(cart);
        editor.putString(KEY_CART, cartJson);
        editor.apply();
    }
    // Di CartManager.java
    public int getCartItemCount() {
        List<Cart> cartItems = getCart();
        return cartItems != null ? cartItems.size() : 0;
    }

    public void addToCart(Cart newItem) {
        List<Cart> cart = getCart();
        boolean itemExists = false;

        // Calculate final price with discount
        double finalPrice = newItem.getPrice();

        for (Cart item : cart) {
            // Check if same product and variant exists
            if (item.getProductId() == newItem.getProductId() &&
                    item.getVariantName().equals(newItem.getVariantName())) {

                // Check if adding quantity exceeds stock
                int newQuantity = item.getQuantity() + 1;
                if (newQuantity <= item.getStock()) {
                    item.setQuantity(newQuantity);
                }
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            // Set final discounted price before adding
            newItem.setPrice(finalPrice);
            cart.add(newItem);
        }

        saveCart(cart);
    }

    public void clearCart() {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY_CART);
        editor.apply();
    }
}