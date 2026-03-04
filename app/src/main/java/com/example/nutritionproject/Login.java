package com.example.nutritionproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nutritionproject.databinding.ActivityLoginBinding;
import com.example.nutritionproject.databinding.ActivitySignupBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.btnLogin.setOnClickListener(v -> {
            i = new Intent(this, MainActivity.class);
            startActivity(i);
        });

        binding.tvSignupRedirect.setOnClickListener(v -> {
            i = new Intent(this, Signup.class);
            startActivity(i);
        });
    }
}