package com.example.jagawarga;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log; // PENTING: Import ini sudah ada
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuatPengumumanActivity extends AppCompatActivity {

    private EditText inputJudul, inputIsi;
    private Button btnSubmit;
    private ImageButton btnBack;

    // Pastikan URL ini sesuai dengan yang kamu pakai sekarang
    private String URL_CREATE = "https://newsletter-cod-jeff-cement.trycloudflare.com/jagawarga/create_pengumuman.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_pengumuman);

        inputJudul = findViewById(R.id.inputJudul);
        inputIsi = findViewById(R.id.inputIsi);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {
            String judul = inputJudul.getText().toString().trim();
            String isi = inputIsi.getText().toString().trim();

            if (judul.isEmpty() || isi.isEmpty()) {
                Toast.makeText(this, "Judul dan Isi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            } else {
                kirimPengumuman(judul, isi);
            }
        });
    }

    private void kirimPengumuman(String judul, String isi) {
        // Ambil ID RT otomatis dari user yang login (Ketua RT)
        String myIdRt = PrefUtils.getIdRt(this);

        if (myIdRt == null) {
            Toast.makeText(this, "Sesi habis, login ulang!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Menerbitkan...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.POST, URL_CREATE,
                response -> {
                    pd.dismiss();

                    // === 1. DEBUG RESPONSE (Cek isi pesan server di Logcat) ===
                    Log.e("DEBUG_PHP", "Raw Response dari Server: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Berhasil diterbitkan!", Toast.LENGTH_LONG).show();
                            finish(); // Kembali ke dashboard
                        } else {
                            Toast.makeText(this, "Gagal: " + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Jika masuk sini, berarti server mengirim HTML (Error PHP), bukan JSON
                        Log.e("JSON_ERROR", "Gagal parsing JSON. Cek Logcat 'DEBUG_PHP'.");
                        Toast.makeText(this, "Terjadi kesalahan di server (Cek Logcat)", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    pd.dismiss();

                    String message = "Gagal koneksi server";

                    // === 2. DEBUG ERROR 500 (Baca pesan error HTML dari PHP) ===
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            // Convert byte data ke String
                            String errorData = new String(error.networkResponse.data, "UTF-8");

                            // Log error mentah ke Logcat
                            Log.e("VOLLEY_ERROR", "Server Error Body: " + errorData);

                            // Coba ambil pesan JSON jika ada, kalau tidak pakai errorData mentah
                            try {
                                JSONObject errorJson = new JSONObject(errorData);
                                message = errorJson.optString("message", errorData);
                            } catch (Exception e) {
                                // Jika gagal parsing JSON (berarti HTML), tampilkan sebagian teks
                                message = "Error Server: Lihat Logcat";
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("judul", judul);
                params.put("isi", isi);
                params.put("id_rt", myIdRt);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}