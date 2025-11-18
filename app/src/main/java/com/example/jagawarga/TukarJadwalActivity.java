package com.example.jagawarga;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

public class TukarJadwalActivity extends AppCompatActivity {

    EditText inputIdJadwalSaya, inputIdJadwalTujuan;
    Button btnRequestTukar;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tukar_jadwal);

        // --- Inisialisasi komponen dari XML ---
        btnBack = findViewById(R.id.btnBackAbsen);
        inputIdJadwalSaya = findViewById(R.id.inputIdJadwalSaya);
        inputIdJadwalTujuan = findViewById(R.id.inputIdJadwalTujuan);
        btnRequestTukar = findViewById(R.id.btnTukarAbsen);

        // --- Tombol Back ke Halaman Sebelumnya ---
        btnBack.setOnClickListener(v -> onBackPressed());

        // --- Logic awal tombol Request Tukar ---
        btnRequestTukar.setOnClickListener(v -> {
            String jadwalSaya = inputIdJadwalSaya.getText().toString().trim();
            String jadwalTujuan = inputIdJadwalTujuan.getText().toString().trim();

            // Validasi sederhana
            if (jadwalSaya.isEmpty()) {
                inputIdJadwalSaya.setError("ID Jadwal Saya harus diisi");
                return;
            }

            if (jadwalTujuan.isEmpty()) {
                inputIdJadwalTujuan.setError("ID Jadwal Tujuan harus diisi");
                return;
            }

            // Nanti logic API / database masuk di sini
            // Contoh sementara:
            // Toast.makeText(this, "Request Tukar berhasil dikirim!", Toast.LENGTH_SHORT).show();
        });
    }
}
