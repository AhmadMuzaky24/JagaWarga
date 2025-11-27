package com.example.jagawarga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        // ====== 1. Greeting nama RT ======
        // Mengambil data yang dikirim dari LoginActivity
        String namaRt = getIntent().getStringExtra("nama_user");
        if (namaRt == null || namaRt.trim().isEmpty()) {
            namaRt = "Pak RT"; // Default jika data kosong
        }

        TextView tvGreetingRt = findViewById(R.id.tvGreetingRt);
        tvGreetingRt.setText("Hai, " + namaRt + " !");

        // ====== 2. Set tanggal hari ini ======
        TextView tvTanggalRt = findViewById(R.id.tvTanggalRt);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String tanggal = sdf.format(calendar.getTime());
        tvTanggalRt.setText(tanggal);

        // ====== 3. Inisialisasi menu ======
        menuTerimaLaporan   = findViewById(R.id.menuTerimaLaporan);
        menuBuatPengumuman  = findViewById(R.id.menuBuatPengumuman);
        menuListPermintaan  = findViewById(R.id.menuListPermintaan);
        btnGenerateJadwal   = findViewById(R.id.btnGenerateJadwal);

        // ====== 4. Setup Listener (Navigasi) ======

        // Menu: Terima Laporan
        menuTerimaLaporan.setOnClickListener(v -> {
            Toast.makeText(DashboardRtActivity.this, "Fitur Terima Laporan akan segera hadir!", Toast.LENGTH_SHORT).show();
            // Nanti jika sudah ada Activity-nya, ganti dengan:
            // startActivity(new Intent(this, TerimaLaporanActivity.class));
        });

        // Menu: Buat Pengumuman
        menuBuatPengumuman.setOnClickListener(v -> {
            // Layout 'activity_buat_pengumuman.xml' sudah ada, tapi Java-nya belum.
            // Nanti buat file 'BuatPengumumanActivity.java' lalu uncomment baris ini:
            // startActivity(new Intent(this, BuatPengumumanActivity.class));
            Toast.makeText(DashboardRtActivity.this, "Fitur Buat Pengumuman akan segera hadir!", Toast.LENGTH_SHORT).show();
        });

        // Menu: List Permintaan (Validasi Warga/Absen)
        menuListPermintaan.setOnClickListener(v -> {
            // Layout 'activity_list_permintaan_register.xml' sudah ada.
            // Nanti buat file 'ListPermintaanActivity.java' lalu uncomment baris ini:
            // startActivity(new Intent(this, ListPermintaanActivity.class));
            Toast.makeText(DashboardRtActivity.this, "Fitur List Permintaan akan segera hadir!", Toast.LENGTH_SHORT).show();
        });

        // Tombol: Generate Jadwal
        // Mengarah ke file uji_coba_generate_jadwal yang SUDAH ADA
        if (btnGenerateJadwal != null) {
            btnGenerateJadwal.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardRtActivity.this, uji_coba_generate_jadwal.class);
                startActivity(intent);
            });
        }
    }
}