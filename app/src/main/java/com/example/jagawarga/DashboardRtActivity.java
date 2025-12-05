package com.example.jagawarga;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardRtActivity extends AppCompatActivity {

    // Menu utama
    private LinearLayout menuTerimaLaporan;
    private LinearLayout menuListPermintaan;

    private LinearLayout menuBuatPengumuman;

    // Tombol generate (sesuai layout XML, ini adalah TextView yang dibungkus CardView/Layout)
    private TextView btnGenerateJadwal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_rt);

        // ====== 1. Greeting nama RT ======
        String namaRt = getIntent().getStringExtra("nama_user");
        if (namaRt == null || namaRt.trim().isEmpty()) {
            namaRt = "Pak RT";
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
        menuListPermintaan  = findViewById(R.id.menuListPermintaan);
        menuBuatPengumuman  = findViewById(R.id.menuBuatPengumuman);
        btnGenerateJadwal   = findViewById(R.id.btnGenerateJadwal);

        // ====== 4. Setup Listener (Navigasi) ======

        menuTerimaLaporan.setOnClickListener(v -> {
            startActivity(new Intent(DashboardRtActivity.this, TerimaLaporanActivity.class));
        });

        menuBuatPengumuman.setOnClickListener(v -> {
            // Arahkan ke halaman Buat Pengumuman
            Intent intent = new Intent(DashboardRtActivity.this, BuatPengumumanActivity.class);
            startActivity(intent);
        });

        menuListPermintaan.setOnClickListener(v -> {
            // Mengarahkan ke ListPermintaan (activity_list_permintaan_register.xml)
            // Pastikan kamu sudah punya Activity Java-nya, jika belum buatlah ListPermintaanActivity
            startActivity(new Intent(this, ListPermintaanActivity.class));
        });

        // ====== 5. LOGIC GENERATE JADWAL (UPDATE) ======
        if (btnGenerateJadwal != null) {
            btnGenerateJadwal.setOnClickListener(v -> {

                // Ambil ID RT dari Shared Preference (Login Session)
                String idRt = PrefUtils.getIdRt(DashboardRtActivity.this);

                if (idRt != null) {
                    // Panggil fungsi generate
                    generateJadwal(idRt);
                } else {
                    Toast.makeText(DashboardRtActivity.this, "ID RT tidak ditemukan, silakan login ulang.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // --- METHOD GENERATE JADWAL (Pindahan dari uji_coba) ---
    private void generateJadwal(String id_rt) {
        String url = "https://liberty-currencies-billion-release.trycloudflare.com/jagawarga/generate_jadwal.php";

        ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Sedang menyusun jadwal...");
        loading.show();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    loading.dismiss();
                    Log.d("API_GENERATE", "Raw Response: " + response);

                    try {
                        JSONObject json = new JSONObject(response);

                        // Tampilkan pesan sukses dari server
                        String message = json.optString("message", "Jadwal berhasil digenerate");
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                        // Opsional: Jika ingin langsung melihat hasilnya, bisa diarahkan ke halaman Jadwal
                        // Intent intent = new Intent(DashboardRtActivity.this, JadwalRondaActivity.class);
                        // startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    loading.dismiss();
                    Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Mengirim parameter id_rt ke PHP
                params.put("id_rt", id_rt);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}