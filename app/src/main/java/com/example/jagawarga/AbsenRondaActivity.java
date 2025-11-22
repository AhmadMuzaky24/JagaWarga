package com.example.jagawarga;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AbsenRondaActivity extends AppCompatActivity {

    // --- field (property) class ---
    private ImageButton btnBackAbsen;
    private Button btnUploadLaporan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // pastikan namanya sama dengan file xml: res/layout/activity_absen_ronda.xml
        setContentView(R.layout.activity_absen_ronda);

        initViews();
        setupListeners();

        //Set tanggal hari ini
        TextView Tanggal_absen = findViewById(R.id.Tanggal_absen);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String tanggal = sdf.format(calendar.getTime());
        Tanggal_absen.setText(tanggal);
    }

    // ---------------- PBO: method terpisah ----------------

    /** Ambil semua view dari XML */
    private void initViews() {
        btnBackAbsen       = findViewById(R.id.btnBackAbsen);
        btnUploadLaporan   = findViewById(R.id.btnUploadLaporan);
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
