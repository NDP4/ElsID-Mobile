package com.mobile2.uts_elsid.api;

import com.google.gson.annotations.SerializedName;
import com.mobile2.uts_elsid.model.User;

import java.util.List;

public class UserResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("users")
    private List<User> users;

    // Getter methods
    public int getStatus() {
        return status;
    }

    public List<User> getUsers() {
        return users;
    }
}