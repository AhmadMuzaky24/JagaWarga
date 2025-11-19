package com.example.jagawarga;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class LaporanKeamananActivity extends AppCompatActivity {

    private Spinner spinnerJenisLaporan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_keamanan); // pastikan nama layout benar

        // inisialisasi view
        spinnerJenisLaporan = findViewById(R.id.spinnerJenisLaporan);
        ImageButton btnBack = findViewById(R.id.btnBackLaporan);

        setupJenisLaporanSpinner();
        setupBackButton(btnBack);
    }

    // --- Logic dropdown Jenis Laporan ---
    private void setupJenisLaporanSpinner() {
        String[] jenisLaporan = new String[]{
                "Keributan",
                "Perusakan",
                "Pencurian",
                "Penculikan",
                "Lainnya"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                jenisLaporan
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerJenisLaporan.setAdapter(adapter);
    }

    // --- Logic tombol back ---
    private void setupBackButton(ImageButton btnBack) {
        if (btnBack == null) return;

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // karena activity ini dipanggil dari Dashboard,
                // finish() akan menutup activity ini dan kembali ke DashboardActivity
                finish();
            }
        });
    }
}
