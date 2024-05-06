package com.example.musicplayercoursework;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicplayercoursework.databinding.ActivitySignupBinding;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                String confirmPassword = binding.confirmPasswordEditText.getText().toString();

                if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                    binding.emailEditText.setError("Неверный email");
                    return;
                }

                if (password.length() < 6) {
                    binding.passwordEditText.setError("Пароль должен быть больше 6-ти символов");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    binding.confirmPasswordEditText.setError("Пароли не совпадают");
                    return;
                }

                createAccountWithFirebase(email, password);
            }
        });

    }

    void createAccountWithFirebase(String email, String password) {
        
    }
}