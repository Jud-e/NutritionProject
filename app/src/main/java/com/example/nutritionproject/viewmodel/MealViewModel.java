package com.example.nutritionproject.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nutritionproject.repository.MealRepository;

import java.util.List;
import java.util.Map;

public class MealViewModel extends AndroidViewModel {

    private final MealRepository mealRepository;
    private final MutableLiveData<Boolean> logResult = new MutableLiveData<>();
    private final MutableLiveData<Integer> dailyCalories = new MutableLiveData<>();
    private final MutableLiveData<List<Map<String, Object>>> breakfastMeals = new MutableLiveData<>();
    private final MutableLiveData<List<Map<String, Object>>> lunchMeals = new MutableLiveData<>();
    private final MutableLiveData<List<Map<String, Object>>> dinnerMeals = new MutableLiveData<>();
    private final MutableLiveData<List<Map<String, Object>>> snackMeals = new MutableLiveData<>();

    public MealViewModel(@NonNull Application application) {
        super(application);
        mealRepository = new MealRepository();
    }

    public void logMeal(String name, int calories, String mealType) {
        mealRepository.logMeal(name, calories, mealType, logResult);
    }

    public void fetchMealsByType(String mealType) {
        switch (mealType) {
            case "breakfast":
                mealRepository.getMealsByType("breakfast", breakfastMeals);
                break;
            case "lunch":
                mealRepository.getMealsByType("lunch", lunchMeals);
                break;
            case "dinner":
                mealRepository.getMealsByType("dinner", dinnerMeals);
                break;
            case "snack":
                mealRepository.getMealsByType("snack", snackMeals);
                break;
        }
    }

    public void fetchDailyCalories() {
        mealRepository.getDailyCalories(dailyCalories);
    }

    public LiveData<Boolean> getLogResult() { return logResult; }
    public LiveData<Integer> getDailyCalories() { return dailyCalories; }
    public LiveData<List<Map<String, Object>>> getBreakfastMeals() { return breakfastMeals; }
    public LiveData<List<Map<String, Object>>> getLunchMeals() { return lunchMeals; }
    public LiveData<List<Map<String, Object>>> getDinnerMeals() { return dinnerMeals; }
    public LiveData<List<Map<String, Object>>> getSnackMeals() { return snackMeals; }
}