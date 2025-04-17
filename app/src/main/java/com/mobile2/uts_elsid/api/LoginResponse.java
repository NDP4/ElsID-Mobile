package com.mobile2.uts_elsid.api;

public class LoginResponse {
    private int status;
    private String message;
    private UserData user;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public UserData getUser() {
        return user;
    }

    public static class UserData {
        private String fullname;
        private String email;
        private String phone;
        private String address;
        private String avatar;

        public String getFullname() {
            return fullname;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public String getAvatar() {
            return avatar;
        }
    }
}