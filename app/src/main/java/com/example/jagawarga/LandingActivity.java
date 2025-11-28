package com.example.jagawarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek apakah user sudah login sebelumnya
        // Jika sudah, langsung arahkan ke Dashboard yang sesuai
        if (checkSession()) {
            return; // Hentikan eksekusi agar layout landing tidak perlu dirender
        }

        setContentView(R.layout.activity_landing);

        Button btnMulai = findViewById(R.id.btnMulai);
        btnMulai.setOnClickListener(v -> {
            // Pindah ke halaman Login
            Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tutup LandingActivity agar tidak bisa kembali dengan tombol Back
        });
    }

    private boolean checkSession() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String savedId = prefs.getString("id", null);
        String savedRole = prefs.getString("role", null);
        String savedNama = prefs.getString("nama", "User");

        if (savedId != null && savedRole != null) {
            redirectDashboard(savedRole, savedNama);
            return true;
        }
        return false;
    }

    private void redirectDashboard(String role, String namaUser) {
        Intent intent;

        if (role.equalsIgnoreCase("KetuaRT")) {
            intent = new Intent(this, DashboardRtActivity.class);
        } else if (role.equalsIgnoreCase("KetuaRW")) {
            intent = new Intent(this, DashboardRwActivity.class);
        } else {
            intent = new Intent(this, DashboardActivity.class);
        }

        intent.putExtra("nama_user", namaUser);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}