package com.example.jagawarga;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jagawarga.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tombol "Masuk"
        binding.btnLogin.setOnClickListener(v -> {
            String phone = binding.inputPhone.getText().toString().trim();
            String pass = binding.inputPassword.getText().toString().trim();

            if (phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

