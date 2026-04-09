package com.example.nutritionproject.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MealRepository {

    private final FirebaseFirestore db;
    private final String userId;

    public MealRepository() {
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User must be logged in to access MealRepository");
        }
        userId = user.getUid();
    }

    public void logMeal(String name, int calories, String mealType, MutableLiveData<Boolean> result) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        Map<String, Object> meal = new HashMap<>();
        meal.put("name", name);
        meal.put("calories", calories);
        meal.put("mealType", mealType);
        meal.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users")
                .document(userId)
                .collection("logs")
                .document(date)
                .collection("meals")
                .add(meal)
                .addOnSuccessListener(ref -> result.setValue(true))
                .addOnFailureListener(e -> {
                    Log.e("MealRepo", "Failed to log meal: " + e.getMessage());
                    result.setValue(false);

                });
    }

    public void getMealsForToday(MutableLiveData<List<Map<String, Object>>> result) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        db.collection("users")
                .document(userId)
                .collection("logs")
                .document(date)
                .collection("meals")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Map<String, Object>> meals = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        meals.add(doc.getData());
                    }
                    result.setValue(meals);
                })
                .addOnFailureListener(e -> result.setValue(null));
    }
    public void getMealsByType(String mealType, MutableLiveData<List<Map<String, Object>>> result) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        db.collection("users")
                .document(userId)
                .collection("logs")
                .document(date)
                .collection("meals")
                .whereEqualTo("mealType", mealType)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Map<String, Object>> meals = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        meals.add(doc.getData());
                    }
                    result.setValue(meals);
                })
                .addOnFailureListener(e -> result.setValue(null));
    }

    public void getDailyCalories(MutableLiveData<Integer> result) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        db.collection("users")
                .document(userId)
                .collection("logs")
                .document(date)
                .collection("meals")
                .get()
                .addOnSuccessListener(snapshot -> {
                    int total = 0;
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Long cal = doc.getLong("calories");
                        if (cal != null) total += cal.intValue();
                    }
                    result.setValue(total);
                })
                .addOnFailureListener(e -> result.setValue(0));
    }
    public void saveCalorieGoal(int goal, MutableLiveData<Boolean> result) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("dailyCalorieGoal", goal);
        db.collection("users").document(userId)
                .collection("profile").document("preferences")
                .set(profile, SetOptions.merge())
                .addOnSuccessListener(unused -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
    }

    public void getCalorieGoal(MutableLiveData<Integer> result) {
        db.collection("users").document(userId)
                .collection("profile").document("preferences")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.getLong("dailyCalorieGoal") != null) {
                        result.setValue(doc.getLong("dailyCalorieGoal").intValue());
                    } else {
                        result.setValue(2000); // sensible default
                    }
                })
                .addOnFailureListener(e -> result.setValue(2000));
    }

    public void getWeeklySummary(MutableLiveData<Map<String, Integer>> result) {
        Map<String, Integer> summaryMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        // Build list of last 7 date strings (today first)
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            dates.add(sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }

        // Use an atomic counter to know when all 7 queries are done
        AtomicInteger remaining = new AtomicInteger(dates.size());

        for (String date : dates) {
            db.collection("users")
                    .document(userId)
                    .collection("logs")
                    .document(date)
                    .collection("meals")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        int total = 0;
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Long c = doc.getLong("calories");
                            if (c != null) total += c.intValue();
                        }
                        summaryMap.put(date, total);
                        if (remaining.decrementAndGet() == 0) {
                            result.postValue(summaryMap);
                        }
                    })
                    .addOnFailureListener(e -> {
                        summaryMap.put(date, 0);
                        if (remaining.decrementAndGet() == 0) {
                            result.postValue(summaryMap);
                        }
                    });
        }
    }
}