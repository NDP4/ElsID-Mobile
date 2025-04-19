package com.mobile2.uts_elsid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WishlistManager {
    private static final String PREF_NAME = "WishlistPrefs";
    private static final String KEY_WISHLIST = "wishlist_items";
    private SharedPreferences preferences;
    private Gson gson;

    public WishlistManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addToWishlist(int productId) {
        List<Integer> wishlist = getWishlist();
        if (!wishlist.contains(productId)) {
            wishlist.add(productId);
            saveWishlist(wishlist);
        }
    }

    public void removeFromWishlist(int productId) {
        List<Integer> wishlist = getWishlist();
        wishlist.remove(Integer.valueOf(productId));
        saveWishlist(wishlist);
    }

    public boolean isInWishlist(int productId) {
        return getWishlist().contains(productId);
    }

    public List<Integer> getWishlist() {
        String json = preferences.getString(KEY_WISHLIST, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Integer>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void saveWishlist(List<Integer> wishlist) {
        String json = gson.toJson(wishlist);
        preferences.edit().putString(KEY_WISHLIST, json).apply();
    }
}