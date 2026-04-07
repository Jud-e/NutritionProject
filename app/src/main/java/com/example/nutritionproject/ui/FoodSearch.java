package com.example.nutritionproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.nutritionproject.FoodAdapter;
import com.example.nutritionproject.databinding.ActivityFoodSearchBinding;
import com.example.nutritionproject.viewmodel.FoodApiService;
import com.example.nutritionproject.viewmodel.FoodItem;
import com.example.nutritionproject.viewmodel.FoodResponse;
import com.example.nutritionproject.viewmodel.MealViewModel;
import com.example.nutritionproject.viewmodel.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodSearch extends AppCompatActivity {

    private ActivityFoodSearchBinding binding;
    private FoodAdapter adapter;
    private MealViewModel mealViewModel;
    private FoodItem lastLoggedItem;
    private List<FoodItem> foodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Initialize ViewModel once
        mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);

        // ✅ Initialize adapter once with dialog
        adapter = new FoodAdapter(foodList, item -> {
            lastLoggedItem = item;
            String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};

            new AlertDialog.Builder(this)
                    .setTitle("Add to which meal?")
                    .setItems(mealTypes, (dialog, which) -> {
                        String selectedMealType = mealTypes[which].toLowerCase();
                        mealViewModel.logMeal(item.getName(), item.getCalories(), selectedMealType);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.btnBack.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                showLoading();
                searchFood(query);
            }
        });

        // ✅ Observe log result using lastLoggedItem
        mealViewModel.getLogResult().observe(this, success -> {
            if (success != null && success) {
                String name = lastLoggedItem != null ? lastLoggedItem.getName() : "Meal";
                Toast.makeText(this, name + " logged!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to log meal", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.btnSearch.setEnabled(false);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.btnSearch.setEnabled(true);
    }

    private void searchFood(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FoodApiService service = retrofit.create(FoodApiService.class);
        service.searchFood(query, 1, 20).enqueue(new Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                runOnUiThread(() -> hideLoading());
                if (response.isSuccessful() && response.body() != null) {
                    foodList.clear();
                    for (Product product : response.body().getProducts()) {
                        String name = product.getProductName();
                        String image = product.getImageUrl();
                        int calories = product.getNutriments() != null
                                ? product.getNutriments().getCalories()
                                : 0;

                        if (name != null && !name.isEmpty()) {
                            foodList.add(new FoodItem(name, calories, image));
                        }
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            }

            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                runOnUiThread(() ->
                        {
                            hideLoading();
                            Toast.makeText(FoodSearch.this,
                                    "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );
            }

        });
    }
}