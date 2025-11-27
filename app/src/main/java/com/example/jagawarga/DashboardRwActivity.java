package com.example.jagawarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardRwActivity extends AppCompatActivity {

    // HEADER
    private TextView tvGreeting, tvSubGreeting, tvTanggalCurrent;
    private FrameLayout profileContainer;
    private ImageView imgProfile;

    // MENU CARD (di dalam cardToday)
    private LinearLayout menuKelolaKetuaRT;   // id: menuTerimaLaporan
    private LinearLayout menuBuatPengumuman; // id: menuBuatPengumuman

    // DATA USER RW (ambil dari SharedPreferences)
    private String idRw;
    private String namaRw;
    private String idRtRw;   // kalau RW punya RT khusus atau bisa kosong

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_rw);

        loadUserData();
        initViews();
        setGreeting();
        setTodayDate();
        setupMenuClick();
    }

    // =======================================
    // LOAD DATA USER RW DARI SHAREDPREF
    // =======================================
    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);

        idRw   = prefs.getString("id", null);
        namaRw = prefs.getString("nama", "Pak RW");
        idRtRw = prefs.getString("id_rt", null);   // optional, kalau mau dipakai

        if (idRw == null) {
            Toast.makeText(this, "Data RW tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show();
        }
    }

    // =======================================
    // INIT VIEW
    // =======================================
    private void initViews() {
        tvGreeting       = findViewById(R.id.tvGreeting);
        tvSubGreeting    = findViewById(R.id.tvSubGreeting);
        tvTanggalCurrent = findViewById(R.id.tanggal_current);

        profileContainer = findViewById(R.id.profileContainer);
        imgProfile       = findViewById(R.id.imgProfile);

        // menu di dalam cardToday
        menuKelolaKetuaRT   = findViewById(R.id.menuTerimaLaporan);   // teks: "Kelola Ketua RT"
        menuBuatPengumuman  = findViewById(R.id.menuBuatPengumuman);  // teks: "Buat Pengumuman"
    }

    private void setGreeting() {
        // contoh: "Hai, Pak RW Budi !"
        tvGreeting.setText("Hai, " + namaRw + " !");
        tvSubGreeting.setText("Sudah siap ronda? 🌙");
    }

    private void setTodayDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String today = sdf.format(cal.getTime());
        tvTanggalCurrent.setText(today);
    }

    // =======================================
    // CLICK MENU
    // =======================================
    private void setupMenuClick() {
        // MENU: Kelola Ketua RT  -> AturRtActivity
        if (menuKelolaKetuaRT != null) {
            menuKelolaKetuaRT.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardRwActivity.this, AturRtPromoteActivity.class);

                // kalau mau kirim data RW ke halaman Atur RT:
                intent.putExtra("id_rw", idRw);
                intent.putExtra("nama_rw", namaRw);
                intent.putExtra("id_rt_rw", idRtRw);

                startActivity(intent);
            });
        }

        // MENU: Buat Pengumuman -> BuatPengumumanActivity
        if (menuBuatPengumuman != null) {
            menuBuatPengumuman.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardRwActivity.this, LaporanKeamananActivity.class);
                // bisa juga kirim nama RW / id RW jika perlu
                intent.putExtra("id_rw", idRw);
                startActivity(intent);
            });
        }
    }
}