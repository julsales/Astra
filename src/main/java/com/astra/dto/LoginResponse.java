package com.astra.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String userType;
    private String email;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message, String userType, String email) {
        this.success = success;
        this.message = message;
        this.userType = userType;
        this.email = email;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
