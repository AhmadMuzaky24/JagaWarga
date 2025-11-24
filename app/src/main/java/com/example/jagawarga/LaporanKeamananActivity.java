package com.example.jagawarga;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagawarga.databinding.ActivityLaporanKeamananBinding;
import com.example.jagawarga.databinding.ActivityLoginBinding;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LaporanKeamananActivity extends AppCompatActivity {

    private Spinner spinnerJenisLaporan;
    private ActivityLaporanKeamananBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_keamanan); // pastikan nama layout benar

        binding = ActivityLaporanKeamananBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // inisialisasi view
        spinnerJenisLaporan = findViewById(R.id.spinnerJenisLaporan);
        ImageButton btnBack = findViewById(R.id.btnBackLaporan);
        EditText inputDetailLaporan = findViewById(R.id.inputDetailLaporan);

        setupJenisLaporanSpinner();
        setupBackButton(btnBack);

        inputDetailLaporan.setScroller(new Scroller(this));
        inputDetailLaporan.setVerticalScrollBarEnabled(true);
        inputDetailLaporan.setMovementMethod(new ScrollingMovementMethod());

        binding.btnUploadLaporanKeamanan.setOnClickListener(v -> {
            String isi = binding.inputDetailLaporan.getText().toString();

            if (isi.isEmpty()) {
                Toast.makeText(this, "Isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show();
            } else {
                kirimLaporan();
            }
        });

        // Set tanggal + waktu sekarang
        TextView inputTanggalLaporan = findViewById(R.id.inputTanggalLaporan);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM, HH:mm 'WIB'", new Locale("id", "ID"));

        String tanggalWaktu = sdf.format(calendar.getTime());
        inputTanggalLaporan.setText(tanggalWaktu);
        inputTanggalLaporan.setFocusable(false);
        inputTanggalLaporan.setClickable(false);


    }

    // --- Logic dropdown Jenis Laporan ---
    private void setupJenisLaporanSpinner() {
        String[] jenisLaporan = new String[]{
                "Keributan",
                "Perusakan",
                "Pencurian",
                "Penculikan",
                "Lainnya"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                jenisLaporan
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerJenisLaporan.setAdapter(adapter);
    }

    // --- Logic tombol back ---
    private void setupBackButton(ImageButton btnBack) {
        if (btnBack == null) return;

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // karena activity ini dipanggil dari Dashboard,
                // finish() akan menutup activity ini dan kembali ke DashboardActivity
                finish();
            }
        });
    }
    private void kirimLaporan() {

        String isi = binding.inputDetailLaporan.getText().toString();
        String jenis = binding.spinnerJenisLaporan.getSelectedItem().toString();

        // ambil id_warga yang disimpan setelah login
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String idWarga = prefs.getString("id", null);

        if (idWarga == null) {
            Toast.makeText(this, "Error: id_warga tidak ditemukan. User belum login?", Toast.LENGTH_LONG).show();
            return;
        }

        String url = "https://intl-edited-sticker-jam.trycloudflare.com/jagawarga/laporan.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API_LAPORAN", response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status").equals("success")) {
                            Toast.makeText(this, "Laporan berhasil dikirim!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },

                error -> {
                    Log.e("API_LAPORAN_ERR", "Error: " + error.toString());
                    Toast.makeText(this, "Gagal mengirim laporan", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id_warga", idWarga);  // dikirim manual (karena Android tidak punya session)
                params.put("isi_laporan", isi);
                params.put("jenis_laporan", jenis);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0 (Android)");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}
