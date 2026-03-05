package com.example.nutritionproject.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MealRepository {

    private final FirebaseFirestore db;
    private final String userId;

    public MealRepository() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                .addOnFailureListener(e -> result.setValue(false));
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

    public void getDailyCalories(MutableLiveData<Integer> result) {
//        getMealsForToday(mealsLiveData -> {
//            // Observer pattern won't work directly here, so we query directly
//        });

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
}
