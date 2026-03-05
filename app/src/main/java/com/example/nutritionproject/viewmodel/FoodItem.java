package com.example.nutritionproject.viewmodel;

public class FoodItem {
    private String name;
    private int calories;
    private String imageUrl;

    public FoodItem(String name, int calories, String imageUrl) {
        this.name = name;
        this.calories = calories;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public int getCalories() { return calories; }
    public String getImageUrl() { return imageUrl; }
}
