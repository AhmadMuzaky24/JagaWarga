package com.example.jagawarga;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AbsenRondaActivity extends AppCompatActivity {

    // --- field (property) class ---
    private ImageButton btnBackAbsen;
    private Button btnUploadLaporan;
    private Spinner spinnerTanggalAbsen, spinnerShift, spinnerLokasiRonda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // pastikan namanya sama dengan file xml: res/layout/activity_absen_ronda.xml
        setContentView(R.layout.activity_absen_ronda);

        initViews();
        setupDropdowns();
        setupListeners();
    }

    // ---------------- PBO: method terpisah ----------------

    /** Ambil semua view dari XML */
    private void initViews() {
        btnBackAbsen       = findViewById(R.id.btnBackAbsen);
        btnUploadLaporan   = findViewById(R.id.btnUploadLaporan);
        spinnerTanggalAbsen = findViewById(R.id.spinnerTanggalAbsen);
        spinnerShift        = findViewById(R.id.spinnerShift);
        spinnerLokasiRonda  = findViewById(R.id.spinnerLokasiRonda);
    }

    /** Set isi dropdown (Spinner) */
    private void setupDropdowns() {
        // Dummy data – nanti bisa kamu ganti dengan data dari server / database
        String[] tanggalOptions = {
                "Pilih Tanggal",
                "1 November 2025",
                "2 November 2025",
                "3 November 2025"
        };

        String[] shiftOptions = {
                "Pilih Shift",
                "Shift 1 (19.00 – 23.00)",
                "Shift 2 (23.00 – 03.00)",
                "Shift 3 (03.00 – 07.00)"
        };

        String[] lokasiOptions = {
                "Pilih Lokasi",
                "Pos Utara",
                "Pos Selatan",
                "Pos Timur",
                "Pos Barat"
        };

        spinnerTanggalAbsen.setAdapter(createSpinnerAdapter(tanggalOptions));
        spinnerShift.setAdapter(createSpinnerAdapter(shiftOptions));
        spinnerLokasiRonda.setAdapter(createSpinnerAdapter(lokasiOptions));
    }

    /** Helper bikin adapter supaya tidak copy-paste */
    private ArrayAdapter<String> createSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    /** Listener tombol back & upload */
    private void setupListeners() {
        // Back ke halaman sebelumnya (DashboardActivity) dengan stack Android biasa
        btnBackAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // cukup finish, otomatis balik ke Dashboard
            }
        });

        btnUploadLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sementara cuma contoh
                Toast.makeText(AbsenRondaActivity.this,
                        "Data absen siap di-upload (dummy)",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
