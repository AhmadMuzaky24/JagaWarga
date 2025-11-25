package com.example.jagawarga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardRtActivity extends AppCompatActivity {

    // Menu utama
    private LinearLayout menuTerimaLaporan;
    private LinearLayout menuBuatPengumuman;
    private LinearLayout menuListPermintaan;

    // Tombol generate
    private TextView btnGenerateJadwal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_rt);

        // ====== Greeting nama RT ======
        String namaRt = getIntent().getStringExtra("nama_user");
        if (namaRt == null || namaRt.trim().isEmpty()) {
            namaRt = "Pak RT";
        }

        TextView tvGreetingRt = findViewById(R.id.tvGreetingRt);
        tvGreetingRt.setText("Hai, " + namaRt + " !");

        // ====== Set tanggal hari ini ======
        TextView tvTanggalRt = findViewById(R.id.tvTanggalRt);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String tanggal = sdf.format(calendar.getTime());
        tvTanggalRt.setText(tanggal);

        // ====== Inisialisasi menu ======
        menuTerimaLaporan   = findViewById(R.id.menuTerimaLaporan);
        menuBuatPengumuman  = findViewById(R.id.menuBuatPengumuman);
        menuListPermintaan  = findViewById(R.id.menuListPermintaan);
        btnGenerateJadwal   = findViewById(R.id.btnGenerateJadwal);

        // ====== Navigasi (PBO style) ======
        setupMenuNavigation(menuTerimaLaporan, RtTerimaLaporanActivity.class);
        setupMenuNavigation(menuBuatPengumuman, RtPengumumanActivity.class);
        setupMenuNavigation(menuListPermintaan, RtListPermintaanActivity.class);

        // tombol generate jadwal (misal ke activity khusus)
        if (btnGenerateJadwal != null) {
            btnGenerateJadwal.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardRtActivity.this,
                        RtGenerateJadwalActivity.class);
                startActivity(intent);
            });
        }
    }

    /**
     * Fungsi reusable untuk semua menu (konsep PBO:
     * satu perilaku generik yang bisa dipakai berkali-kali).
     */
    private void setupMenuNavigation(LinearLayout menuView, final Class<?> targetActivity) {
        if (menuView == null) return;

        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardRtActivity.this, targetActivity);
                startActivity(intent);
            }
        });
    }
}
