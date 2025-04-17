package com.mobile2.uts_elsid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.mobile2.uts_elsid.api.LoginResponse;

public class SessionManager {
    private static final String PREF_NAME = "LoginSession";
    private static final String KEY_USER_DATA = "userData";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void saveLoginSession(LoginResponse loginResponse) {
        editor.putString(KEY_USER_DATA, gson.toJson(loginResponse));
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public LoginResponse getUserData() {
        String userData = pref.getString(KEY_USER_DATA, null);
        return userData != null ? gson.fromJson(userData, LoginResponse.class) : null;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}