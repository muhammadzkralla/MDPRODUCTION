package com.vuducminh.nicefood.model;

public class BraintreeToken {
    private boolean error;
    private String token;

    public BraintreeToken() {
    }

    public BraintreeToken(boolean error, String token) {
        this.error = error;
        this.token = token;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
