package com.example.jagawarga;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

        boolean[] isPasswordVisible = {false};

        binding.btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                // Hide password
                binding.inputPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                binding.btnTogglePassword.setImageResource(R.drawable.outline_key_24);
            } else {
                // Show password
                binding.inputPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                binding.btnTogglePassword.setImageResource(R.drawable.outline_key_off_24);
            }

            // Agar cursor tetap di akhir teks
            binding.inputPassword.setSelection(binding.inputPassword.getText().length());

            isPasswordVisible[0] = !isPasswordVisible[0];
        });


        // Tombol "Masuk"
        binding.btnLogin.setOnClickListener(v -> {
            String phone = binding.inputPhone.getText().toString().trim();
            String pass = binding.inputPassword.getText().toString().trim();

            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); // opsional: supaya user tidak kembali ke login setelah tekan back

            if (phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnMasukTab2.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // opsional: supaya user tidak kembali ke login setelah tekan back
        });


    }
}

