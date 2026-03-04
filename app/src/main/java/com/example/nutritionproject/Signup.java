package com.example.nutritionproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nutritionproject.databinding.ActivitySignupBinding;

public class Signup extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            if (binding.edPasswordSignup.getText().toString()
                    .equals(binding.edPasswordRepeat.getText().toString())){
                Intent i = new Intent(this, Login.class);
                startActivity(i);
            }
            else {
                Toast.makeText(this, "Your Passwords aren't matching", Toast.LENGTH_SHORT).show();
            }
        });






        binding.tvLoginRedirect.setOnClickListener(v -> {
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        });
    }
}