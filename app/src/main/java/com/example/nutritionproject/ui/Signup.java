package com.example.nutritionproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutritionproject.R;
import com.example.nutritionproject.databinding.ActivitySignupBinding;
import com.example.nutritionproject.viewmodel.AuthViewModel;

public class Signup extends AppCompatActivity {

    private ActivitySignupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //need to fix this later:
        binding.btnSignup.setOnClickListener(v -> {
            String email = binding.edEmailSignup.getText().toString().trim();
            Log.d("DEBUG",email);
            String password = binding.edPasswordSignup.getText().toString().trim();
            if (email.trim().isEmpty() ||binding.edPasswordSignup.toString().trim().isEmpty() ){
                Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.register(email,password);
        });
        authViewModel.getAuthResult().observe(this, success -> {
            if(success != null && success) {
                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Signup failed", Toast.LENGTH_SHORT).show();
            }
        });





        binding.tvLoginRedirect.setOnClickListener(v -> {
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        });
    }
}