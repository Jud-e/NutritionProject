package com.example.nutritionproject.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutritionproject.databinding.ActivityMainBinding;
import com.example.nutritionproject.viewmodel.MealViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MealViewModel mealViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);

        binding.btnFoodSearch.setOnClickListener(v ->
                startActivity(new Intent(this, FoodSearch.class))
        );

        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        });

        mealViewModel.getDailyCalories().observe(this, total -> {
            if (total != null) {
                binding.calorieText.setText(total + " kcal");
            }
        });

        mealViewModel.getBreakfastMeals().observe(this, meals ->
                populateMealSection(binding.llBreakfastItems, binding.tvBreakfastTotal, meals)
        );

        mealViewModel.getLunchMeals().observe(this, meals ->
                populateMealSection(binding.llLunchItems, binding.tvLunchTotal, meals)
        );

        mealViewModel.getDinnerMeals().observe(this, meals ->
                populateMealSection(binding.llDinnerItems, binding.tvDinnerTotal, meals)
        );

        mealViewModel.getSnackMeals().observe(this, meals ->
                populateMealSection(binding.llSnackItems, binding.tvSnackTotal, meals)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mealViewModel.fetchMealsByType("breakfast");
        mealViewModel.fetchMealsByType("lunch");
        mealViewModel.fetchMealsByType("dinner");
        mealViewModel.fetchMealsByType("snack");
        mealViewModel.fetchDailyCalories();
    }

    private void populateMealSection(LinearLayout container, TextView totalView,
                                     List<Map<String, Object>> meals) {
        container.removeAllViews();
        if (meals == null || meals.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No items logged");
            empty.setTextColor(Color.GRAY);
            empty.setTextSize(13);
            container.addView(empty);
            totalView.setText("0 kcal");
            return;
        }

        int total = 0;
        for (Map<String, Object> meal : meals) {
            String name = (String) meal.get("name");
            Long cal = (Long) meal.get("calories");
            int calories = cal != null ? cal.intValue() : 0;
            total += calories;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 4, 0, 4);

            TextView tvName = new TextView(this);
            tvName.setText(name);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvCal = new TextView(this);
            tvCal.setText(calories + " kcal");
            tvCal.setTextColor(Color.GRAY);

            row.addView(tvName);
            row.addView(tvCal);
            container.addView(row);
        }

        totalView.setText(total + " kcal");
    }
}