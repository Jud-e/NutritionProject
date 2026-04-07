package com.example.nutritionproject.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutritionproject.databinding.ActivityMainBinding;
import com.example.nutritionproject.viewmodel.MealViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
        mealViewModel.getCalorieGoal().observe(this, goal -> {
            binding.tvCalorieGoal.setText("Goal: " + goal + " kcal");
            // optional: update a progress bar
        });
        mealViewModel.getWeeklySummary().observe(this, summary -> {
            if (summary == null) return;
            binding.llWeeklySummary.removeAllViews();

            List<String> sortedDates = new ArrayList<>(summary.keySet());
            Collections.sort(sortedDates, Collections.reverseOrder()); // newest first

            String[] labels = {"Today", "Yesterday", null};
            SimpleDateFormat displayFmt = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
            int goal = 2000; // replace with live calorieGoal value if you have it

            for (int i = 0; i < sortedDates.size(); i++) {
                String date = sortedDates.get(i);
                int calories = summary.getOrDefault(date, 0);
                boolean isOver = calories > goal;

                View card = buildSummaryCard(date, labels[i], calories, goal, isOver, displayFmt);
                binding.llWeeklySummary.addView(card);
            }
        });

// Long-press or settings button to edit goal
        binding.btnEditGoal.setOnClickListener(v -> showGoalDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mealViewModel.fetchMealsByType("breakfast");
        mealViewModel.fetchMealsByType("lunch");
        mealViewModel.fetchMealsByType("dinner");
        mealViewModel.fetchMealsByType("snack");
        mealViewModel.fetchDailyCalories();
        mealViewModel.fetchCalorieGoal();
        mealViewModel.fetchWeeklySummary();
    }
    private void showGoalDialog() {
        EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("e.g. 2000");

        new AlertDialog.Builder(this)
                .setTitle("Set Daily Calorie Goal")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String val = input.getText().toString().trim();
                    if (!val.isEmpty()) {
                        mealViewModel.saveCalorieGoal(Integer.parseInt(val));
                        Toast.makeText(this, "Goal saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
    private View buildSummaryCard(String date, String label, int calories,
                                  int goal, boolean isOver, SimpleDateFormat fmt) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(36, 28, 36, 28);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#F7F7F8"));
        bg.setCornerRadius(24f);
        bg.setStroke(2, Color.parseColor("#E5E5E5"));
        card.setBackground(bg);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 20);
        card.setLayoutParams(cardParams);

        // Header row
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout labelGroup = new LinearLayout(this);
        labelGroup.setOrientation(LinearLayout.VERTICAL);
        labelGroup.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvLabel = new TextView(this);
        String dayLabel;
        if (label != null) {
            dayLabel = label;
        } else {
            try {
                dayLabel = fmt.format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date));
            } catch (Exception e) {
                dayLabel = date;
            }
        }
        tvLabel.setText(dayLabel);
        tvLabel.setTextSize(14); tvLabel.setTextColor(Color.BLACK); tvLabel.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDate = new TextView(this);
        try { tvDate.setText(fmt.format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date))); }
        catch (Exception e) { tvDate.setText(date); }
        tvDate.setTextSize(12); tvDate.setTextColor(Color.GRAY);

        labelGroup.addView(tvLabel);
        labelGroup.addView(tvDate);

        TextView tvCal = new TextView(this);
        tvCal.setText(String.format(Locale.getDefault(), "%,d kcal", calories));
        tvCal.setTextSize(14); tvCal.setTypeface(null, android.graphics.Typeface.BOLD);
        tvCal.setTextColor(isOver ? Color.parseColor("#EF4444") : Color.parseColor("#16A34A"));

        header.addView(labelGroup);
        header.addView(tvCal);

        // Progress bar
        FrameLayout track = new FrameLayout(this);
        LinearLayout.LayoutParams trackParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 14);
        trackParams.setMargins(0, 16, 0, 8);
        track.setLayoutParams(trackParams);
        GradientDrawable trackBg = new GradientDrawable();
        trackBg.setColor(Color.parseColor("#E5E5E5"));
        trackBg.setCornerRadius(99f);
        track.setBackground(trackBg);

        View fill = new View(this);
        float pct = Math.min((float) calories / goal, 1f);
        fill.setLayoutParams(new FrameLayout.LayoutParams(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.75f * pct), 14));
        GradientDrawable fillBg = new GradientDrawable();
        fillBg.setColor(isOver ? Color.parseColor("#F87171") : Color.parseColor("#4ADE80"));
        fillBg.setCornerRadius(99f);
        fill.setBackground(fillBg);
        track.addView(fill);

        // Footer
        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        int diff = Math.abs(calories - goal);
        TextView tvDiff = new TextView(this);
        tvDiff.setText(String.format(Locale.getDefault(), "%,d kcal %s goal", diff, isOver ? "over" : "under"));
        tvDiff.setTextSize(11); tvDiff.setTextColor(Color.GRAY);
        tvDiff.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView tvGoal = new TextView(this);
        tvGoal.setText("Goal: " + goal);
        tvGoal.setTextSize(11); tvGoal.setTextColor(Color.GRAY);
        footer.addView(tvDiff); footer.addView(tvGoal);

        card.addView(header); card.addView(track); card.addView(footer);
        return card;
    }
}