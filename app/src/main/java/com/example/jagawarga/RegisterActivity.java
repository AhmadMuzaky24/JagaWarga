package com.example.jagawarga;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jagawarga.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // tombol untuk kembali ke Login
        binding.btnMasukTab.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        boolean[] isPasswordVisible = {false};

        binding.btnTogglePassword2.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                // Hide password
                binding.inputPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                binding.btnTogglePassword2.setImageResource(R.drawable.outline_key_24);
            } else {
                // Show password
                binding.inputPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                binding.btnTogglePassword2.setImageResource(R.drawable.outline_key_off_24);
            }

            // Agar cursor tetap di akhir teks
            binding.inputPassword.setSelection(binding.inputPassword.getText().length());

            isPasswordVisible[0] = !isPasswordVisible[0];
        });

        binding.btnRegister.setOnClickListener(v -> {

            // Ambil data form (opsional)
            String nama = binding.inputNama.getText().toString().trim();
            String phone = binding.inputPhone2.getText().toString().trim();
            String nik = binding.inputPhone4.getText().toString().trim();
            String pass = binding.inputPassword.getText().toString().trim();

            // Validasi sederhana
            if (nama.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tampilkan popup
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Pendaftaran Dikirim")
                    .setMessage("Pendaftaran sudah dikirim, silahkan tunggu.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();

                        // Arahkan kembali ke login setelah user klik OK
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .show();
        });

    }
}

