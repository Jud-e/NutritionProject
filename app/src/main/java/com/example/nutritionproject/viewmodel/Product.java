package com.example.nutritionproject.viewmodel;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("product_name")
    private String productName;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("nutriments")
    private Nutrients nutrients;

    public String getProductName() { return productName; }
    public String getImageUrl() { return imageUrl; }
    public Nutrients getNutriments() { return nutrients; }
}
