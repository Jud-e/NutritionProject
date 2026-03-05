package com.example.nutritionproject.viewmodel;

import com.google.gson.annotations.SerializedName;

public class Nutrients {
        @SerializedName("energy-kcal_100g")
        private float calories;

        public int getCalories() { return (int) calories; }
    }

