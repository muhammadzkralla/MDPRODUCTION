package com.dimits.mahalladelivery.model;

public class AddonModel {
    private String name;
    private Long price;

    public AddonModel() {
    }

    public AddonModel(String name, Long price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
