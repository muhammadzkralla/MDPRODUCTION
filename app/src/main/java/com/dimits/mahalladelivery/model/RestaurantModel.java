package com.dimits.mahalladelivery.model;

public class RestaurantModel {
    private String uid,name,address,paymenUrl,imageUrl,phone,active;

    public RestaurantModel(){

    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymenUrl() {
        return paymenUrl;
    }

    public void setPaymenUrl(String paymenUrl) {
        this.paymenUrl = paymenUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
