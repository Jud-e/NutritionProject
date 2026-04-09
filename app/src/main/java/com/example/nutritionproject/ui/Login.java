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
import com.example.nutritionproject.databinding.ActivityLoginBinding;
import com.example.nutritionproject.viewmodel.AuthViewModel;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edEmailLogin.getText().toString().trim();
            Log.d("DEBUG",email);
            String password = binding.edPasswordLogin.getText().toString().trim();
            if (email.isEmpty() ||password.isEmpty() ){
                Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.login(email,password);
        });
        authViewModel.getAuthResult().observe(this, success -> {
            if(success != null && success) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Login failed. Check your email and password.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvSignupRedirect.setOnClickListener(v -> {
            i = new Intent(this, Signup.class);
            startActivity(i);
        });
    }
}