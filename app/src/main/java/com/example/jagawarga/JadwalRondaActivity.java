package com.example.jagawarga;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class JadwalRondaActivity extends AppCompatActivity {

    // === UI Jadwal ===
    TextView[] tvNama = new TextView[10];
    TextView[] tvIdJadwal = new TextView[10];
    TextView[] tvJam = new TextView[10];

    // === UI Navigasi Tanggal ===
    ImageButton btnBackJadwal, btnPrevDate, btnNextDate;
    Button btnKembaliJadwal;
    TextView textTanggalPilihan;

    // === Date Management ===
    Calendar calendar;
    SimpleDateFormat dateFormatAPI;      // format kirim ke API
    SimpleDateFormat dateFormatDisplay;  // format tampil ke UI

    // === URL CLOUDFARE TUNNEL ===
    private final String BASE_URL = "https://newsletter-cod-jeff-cement.trycloudflare.com/jagawarga/";
    // contoh endpoint yang kamu buat → getjadwal.php?tanggal=YYYY-MM-DD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_ronda);

        initUI();
        initTanggal();
        setupListeners();

        // Ambil JSON awal dari Intent → saat buka dari Dashboard
        String json = getIntent().getStringExtra("json_jadwal");
        if (json != null) {
            parseAndApplyJSON(json);
        } else {
            loadJadwalFromAPI(); // fallback jika Intent kosong
        }
    }

    // ============================================================
    // INIT
    // ============================================================
    private void initUI() {

        // init tombol & tanggal
        btnBackJadwal = findViewById(R.id.btnBackJadwal);
        btnPrevDate   = findViewById(R.id.btnPrevDate);
        btnNextDate   = findViewById(R.id.btnNextDate);
        btnKembaliJadwal = findViewById(R.id.btnKembaliJadwal);
        textTanggalPilihan = findViewById(R.id.textTanggalPilihan);

        // init semua TextView jadwal
        tvNama[0] = findViewById(R.id.textNama1);
        tvNama[1] = findViewById(R.id.textNama2);
        tvNama[2] = findViewById(R.id.textNama3);
        tvNama[3] = findViewById(R.id.textNama4);
        tvNama[4] = findViewById(R.id.textNama5);
        tvNama[5] = findViewById(R.id.textNama6);
        tvNama[6] = findViewById(R.id.textNama7);
        tvNama[7] = findViewById(R.id.textNama8);
        tvNama[8] = findViewById(R.id.textNama9);
        tvNama[9] = findViewById(R.id.textNama10);

        tvIdJadwal[0] = findViewById(R.id.textIdJadwal1);
        tvIdJadwal[1] = findViewById(R.id.textIdJadwal2);
        tvIdJadwal[2] = findViewById(R.id.textIdJadwal3);
        tvIdJadwal[3] = findViewById(R.id.textIdJadwal4);
        tvIdJadwal[4] = findViewById(R.id.textIdJadwal5);
        tvIdJadwal[5] = findViewById(R.id.textIdJadwal6);
        tvIdJadwal[6] = findViewById(R.id.textIdJadwal7);
        tvIdJadwal[7] = findViewById(R.id.textIdJadwal8);
        tvIdJadwal[8] = findViewById(R.id.textIdJadwal9);
        tvIdJadwal[9] = findViewById(R.id.textIdJadwal10);

        tvJam[0] = findViewById(R.id.textJam1);
        tvJam[1] = findViewById(R.id.textJam2);
        tvJam[2] = findViewById(R.id.textJam3);
        tvJam[3] = findViewById(R.id.textJam4);
        tvJam[4] = findViewById(R.id.textJam5);
        tvJam[5] = findViewById(R.id.textJam6);
        tvJam[6] = findViewById(R.id.textJam7);
        tvJam[7] = findViewById(R.id.textJam8);
        tvJam[8] = findViewById(R.id.textJam9);
        tvJam[9] = findViewById(R.id.textJam10);
    }

    private void initTanggal() {
        calendar = Calendar.getInstance();

        // Format kirim API → YYYY-MM-DD
        dateFormatAPI = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Format tampilan → Senin, 25 November
        dateFormatDisplay = new SimpleDateFormat("EEEE, dd MMMM", new Locale("id", "ID"));

        updateTanggalUI();
    }

    private void setupListeners() {

        View.OnClickListener back = v -> finish();
        btnBackJadwal.setOnClickListener(back);
        btnKembaliJadwal.setOnClickListener(back);

        btnPrevDate.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            updateTanggalUI();
            loadJadwalFromAPI();
        });

        btnNextDate.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            updateTanggalUI();
            loadJadwalFromAPI();
        });
    }

    private void updateTanggalUI() {
        String formatted = dateFormatDisplay.format(calendar.getTime());
        formatted = formatted.substring(0,1).toUpperCase() + formatted.substring(1);
        textTanggalPilihan.setText(formatted);
    }

    // ============================================================
    // PANGGIL API CLOUDFARE
    // ============================================================
    private void loadJadwalFromAPI() {

        String tanggal = dateFormatAPI.format(calendar.getTime());
        String idRt = PrefUtils.getIdRt(this);
        String url = BASE_URL + "get_jadwal.php?tanggal=" + tanggal + "&id_rt=" + idRt;

        Log.d("API_JADWAL", "CALL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Sukses
                    parseAndApplyJSON(response.toString());
                },
                error -> {
                    // Error
                    Log.e("API_ERROR", error.toString());
                }
        );

        queue.add(req);
    }


    // ============================================================
    // PARSE JSON → TAMPILKAN KE UI
    // ============================================================
    private void parseAndApplyJSON(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("data");

            int limit = Math.min(arr.length(), 10);

            for (int i = 0; i < limit; i++) {
                JSONObject item = arr.getJSONObject(i);

                tvNama[i].setText(item.getString("nama"));
                tvIdJadwal[i].setText("ID Jadwal: " + item.getString("id_jadwal"));
                tvJam[i].setText(item.getString("shift"));
            }

            // sisanya kosongkan
            for (int i = limit; i < 10; i++) {
                tvNama[i].setText("-");
                tvIdJadwal[i].setText("ID Jadwal: -");
                tvJam[i].setText("-");
            }

        } catch (Exception e) {
            Log.e("JSON_ERROR", e.getMessage());
        }
    }
}




