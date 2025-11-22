package com.example.jagawarga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    // properti (konsep PBO: simpan state objek di dalam class)
    private LinearLayout menuAbsen;
    private LinearLayout menuTukar;
    private LinearLayout menuLapor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        String namaUser = getIntent().getStringExtra("nama_user");
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        tvGreeting.setText("Hai, " + namaUser + " !");

        // inisialisasi view
        menuAbsen = findViewById(R.id.menuAbsen);
        menuTukar = findViewById(R.id.menuTukar);     // pastikan ada di XML
        menuLapor = findViewById(R.id.menuLapor);     // pastikan ada di XML

        // atur navigasi tiap menu (tetap PBO: pakai 1 method reusable)
        setupMenuNavigation(menuAbsen, AbsenRondaActivity.class);
        setupMenuNavigation(menuTukar, TukarJadwalActivity.class);
        setupMenuNavigation(menuLapor, LaporanKeamananActivity.class);
    }

    /**
     * Method bantu untuk memasang onClick ke LinearLayout menu,
     * supaya tidak copy–paste kode yang sama berkali-kali.
     */
    private void setupMenuNavigation(LinearLayout menuView, final Class<?> targetActivity) {
        if (menuView == null) return; // jaga-jaga kalau id belum dibuat

        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, targetActivity);
                startActivity(intent);
            }
        });
    }
}
