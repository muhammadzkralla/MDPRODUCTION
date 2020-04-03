package com.vuducminh.nicefood.model;

public class PopluarCategoryModel {
    private String menu_id,food_id,name,image;

    public PopluarCategoryModel() {
    }

    public PopluarCategoryModel(String menu_id, String food_id, String name, String image) {
        this.menu_id = menu_id;
        this.food_id = food_id;
        this.name = name;
        this.image = image;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
