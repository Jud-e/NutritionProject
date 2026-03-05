package com.example.nutritionproject.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.nutritionproject.FoodAdapter;
import com.example.nutritionproject.R;
import com.example.nutritionproject.databinding.ActivityFoodSearchBinding;
import com.example.nutritionproject.viewmodel.FoodApiService;
import com.example.nutritionproject.viewmodel.FoodItem;
import com.example.nutritionproject.viewmodel.FoodResponse;
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
    private List<FoodItem> foodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new FoodAdapter(foodList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.etSearch.getText().toString().trim();
            if (!query.isEmpty()) searchFood(query);
        });
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
                        Toast.makeText(FoodSearch.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}