package com.example.jagawarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    // UI Components
    private LinearLayout menuAbsen, menuTukar, menuLapor, menuJadwal;
    private TextView tvGreeting, tanggalCurrent;

    // Contact card
    private TextView tvContactNumber;
    private TextView tvContactLocation;
    private ImageView imgWhatsapp;
    private View layoutContactCard;

    // User Data
    private String idWarga, idRt, namaUser;

    // Phone pos ronda aktif (untuk WhatsApp)
    private String currentPosPhone = null;

    // API Endpoint (Cloudflared)
    private static final String BASE_URL = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/";
    private static final String GET_POS_RONDA_URL = BASE_URL + "get_pos_ronda.php";

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

        // --- SETUP CONTACT CARD (WHATSAPP) ---
        setupContactCard();
        loadPosRondaForUser();     // ambil nomor pos berdasarkan idRt
    }

    // ======================================================
    // ===============    USER DATA LOADING   ===============
    // ======================================================

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);

        idWarga   = prefs.getString("id", null);
        idRt      = prefs.getString("id_rt", null);
        namaUser  = prefs.getString("nama", "Pengguna");

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

        // contact card (pastikan ID sama dengan di XML)
        tvContactNumber   = findViewById(R.id.tvContactNumberText);
        tvContactLocation = findViewById(R.id.tvContactLocation);
        imgWhatsapp   = findViewById(R.id.imgWhatsapp);
        layoutContactCard = findViewById(R.id.layoutContactContent);
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
            intent.putExtra("id_rt", idRt);
            intent.putExtra("id_warga", idWarga);
            intent.putExtra("nama", namaUser);
            startActivity(intent);
        });

        // khusus menu jadwal: tetap panggil API dulu (logika lama kamu)
        if (menu == menuJadwal) {
            menuJadwal.setOnClickListener(v -> {
                String todayForApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Calendar.getInstance().getTime());
                callApiCloudflare(idRt, todayForApi);
            });
        }
    }

    // ======================================================
    // ===============        API JADWAL      ===============
    // ======================================================

    public void callApiCloudflare(String id_rt, String tanggal) {
        String url = BASE_URL + "get_jadwal.php?id_rt=" + id_rt + "&tanggal=" + tanggal;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("API_RESPONSE", response.toString());

                    Intent i = new Intent(DashboardActivity.this, JadwalRondaActivity.class);
                    i.putExtra("json_jadwal", response.toString());
                    startActivity(i);
                },
                error -> {
                    Toast.makeText(this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                });

        queue.add(req);
    }

    // ======================================================
    // ===============   CONTACT CARD (WA)    ===============
    // ======================================================

    private void setupContactCard() {
        if (imgWhatsapp != null) {
            imgWhatsapp.setOnClickListener(v -> openWhatsapp());
        }
    }

    private void openWhatsapp() {
        if (currentPosPhone == null || currentPosPhone.isEmpty()) {
            Toast.makeText(this, "Nomor pos ronda belum tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        // bersihkan: hanya angka
        String raw = currentPosPhone.replaceAll("[^0-9]", "");

        // ubah ke format internasional Indonesia (62)
        String international;
        if (raw.startsWith("0")) {
            international = "62" + raw.substring(1);
        } else {
            international = raw;
        }

        String url = "https://wa.me/" + international;
        Log.d("WA_DEBUG", "raw=" + raw + ", international=" + international + ", url=" + url);

        // 1) Coba buka langsung di aplikasi WhatsApp
        try {
            Intent waIntent = new Intent(Intent.ACTION_VIEW);
            waIntent.setData(Uri.parse(url));
            waIntent.setPackage("com.whatsapp"); // paksa ke WhatsApp resmi

            startActivity(waIntent);
            return; // kalau berhasil, stop di sini
        } catch (Exception e) {
            e.printStackTrace();
            // lanjut ke fallback
        }

        // 2) Fallback: coba buka via browser biasa
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Tidak ada aplikasi untuk membuka WhatsApp / browser",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void loadPosRondaForUser() {
        if (idRt == null || idRt.isEmpty()) return;

        StringRequest request = new StringRequest(
                Request.Method.POST,
                GET_POS_RONDA_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);

                        if (success) {
                            String nomor  = json.optString("nomor_telepon", "");
                            String lokasi = json.optString("lokasi_pos", "");

                            currentPosPhone = nomor;

                            if (tvContactNumber != null) {
                                tvContactNumber.setText(nomor);
                            }
                            if (tvContactLocation != null) {
                                tvContactLocation.setText(lokasi);
                            }
                        } else {
                            String message = json.optString("message", "Data pos ronda tidak ditemukan");
                            Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(DashboardActivity.this,
                                "Response pos ronda tidak valid", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(DashboardActivity.this,
                            "Gagal mengambil data pos ronda", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_rt", idRt);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}

