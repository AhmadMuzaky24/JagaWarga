package com.example.jagawarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    // UI Components
    private LinearLayout menuAbsen, menuTukar, menuLapor, menuJadwal;
    private TextView tvGreeting, tanggalCurrent;

    // User Data
    private String idWarga, idRt, namaUser;

    // API Endpoint (Cloudflared)
    private static final String BASE_URL = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/";
    // Ganti dengan URL kamu sendiri

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // --- LOAD USER DATA DARI PREF ---
        loadUserData();

        // --- INIT UI ---
        initViews();
        setTodayDate();
        setGreeting();

        // --- SETUP MENU NAVIGATION ---
        setupMenuNavigation(menuAbsen, AbsenRondaActivity.class);
        setupMenuNavigation(menuTukar, TukarJadwalActivity.class);
        setupMenuNavigation(menuLapor, LaporanKeamananActivity.class);
        setupMenuNavigation(menuJadwal, JadwalRondaActivity.class);
    }

    // ======================================================
    // ===============    USER DATA LOADING   ===============
    // ======================================================

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);

        idWarga   = prefs.getString("id", null);
        idRt      = prefs.getString("id_rt", null);
        namaUser  = prefs.getString("nama", "Pengguna");

        // validasi minimal
        if (idWarga == null || idRt == null) {
            Toast.makeText(this, "Data login tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }
    }

    // ======================================================
    // ===============        INIT VIEW        ===============
    // ======================================================

    private void initViews() {
        menuAbsen   = findViewById(R.id.menuAbsen);
        menuTukar   = findViewById(R.id.menuTukar);
        menuLapor   = findViewById(R.id.menuLapor);
        menuJadwal  = findViewById(R.id.menuJadwal);

        tvGreeting       = findViewById(R.id.tvGreeting);
        tanggalCurrent   = findViewById(R.id.tanggal_current);
    }

    private void setGreeting() {
        tvGreeting.setText("Hai, " + namaUser + " !");
    }

    private void setTodayDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String today = sdf.format(cal.getTime());
        tanggalCurrent.setText(today);
    }

    // ======================================================
    // ===============         NAVIGATION     ===============
    // ======================================================

    private void setupMenuNavigation(LinearLayout menu, Class<?> targetActivity) {
        if (menu == null) return;
        menu.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, targetActivity);
            // Kirim data global juga kalau perlu:
            intent.putExtra("id_rt", idRt);
            intent.putExtra("id_warga", idWarga);
            intent.putExtra("nama", namaUser);
            startActivity(intent);
        });

        menuJadwal.setOnClickListener(v -> {
            String todayForApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Calendar.getInstance().getTime());

            callApiCloudflare(idRt, todayForApi);
        });

    }

    // ======================================================
    // ===============        API CALLING     ===============
    // ======================================================

    // Ready-to-use helper jika kamu mau panggil API Cloudflared kemudian:
    public void callApiCloudflare(String id_rt, String tanggal) {
        String url = BASE_URL + "get_jadwal.php?id_rt=" + id_rt + "&tanggal=" + tanggal;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("API_RESPONSE", response.toString());

                    // === PINDAH KE ACTIVITY ===
                    Intent i = new Intent(DashboardActivity.this, JadwalRondaActivity.class);

                    // kirim JSON ke activity
                    i.putExtra("json_jadwal", response.toString());
                    startActivity(i);
                },
                error -> {
                    Toast.makeText(this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                });

        queue.add(req);
    }
}
