package com.example.nutritionproject.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nutritionproject.repository.AuthRepository;


public class AuthViewModel extends ViewModel {

    private AuthRepository repository;
    private MutableLiveData<Boolean> authResult = new MutableLiveData<>();

    public AuthViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<Boolean> getAuthResult() {
        return authResult;
    }

    public void register(String email, String password) {
        repository.register(email, password, authResult);
    }

    public void login(String email,String password){
        repository.login(email,password,authResult);
    }
}