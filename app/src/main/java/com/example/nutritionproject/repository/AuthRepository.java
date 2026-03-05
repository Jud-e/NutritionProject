package com.example.nutritionproject.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

public class AuthRepository {

    private FirebaseAuth mAuth;

    public AuthRepository() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void register(String email, String password, MutableLiveData<Boolean> result){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task->{
            result.setValue(task.isSuccessful());
        });
    }

    public void login(String email, String password, MutableLiveData<Boolean> result) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    result.setValue(task.isSuccessful());
                });
    }
}
