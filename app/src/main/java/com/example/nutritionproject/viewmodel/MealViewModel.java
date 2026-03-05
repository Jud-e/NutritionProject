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
    private final MutableLiveData<List<Map<String, Object>>> todayMeals = new MutableLiveData<>();
    private final MutableLiveData<Integer> dailyCalories = new MutableLiveData<>();

    public MealViewModel(@NonNull Application application) {
        super(application);
        mealRepository = new MealRepository();
    }

    public void logMeal(String name, int calories, String mealType) {
        mealRepository.logMeal(name, calories, mealType, logResult);
    }

    public void fetchTodayMeals() {
        mealRepository.getMealsForToday(todayMeals);
    }

    public void fetchDailyCalories() {
        mealRepository.getDailyCalories(dailyCalories);
    }

    public LiveData<Boolean> getLogResult() { return logResult; }
    public LiveData<List<Map<String, Object>>> getTodayMeals() { return todayMeals; }
    public LiveData<Integer> getDailyCalories() { return dailyCalories; }
}