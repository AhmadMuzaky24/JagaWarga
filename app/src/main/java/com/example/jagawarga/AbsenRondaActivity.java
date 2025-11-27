package com.example.jagawarga;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

public class AbsenRondaActivity extends AppCompatActivity {

    // --- field (property) class ---
    private ImageButton btnBackAbsen;
    private Button btnKirimAbsen;
    private EditText insert_absenID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // pastikan namanya sama dengan file xml: res/layout/activity_absen_ronda.xml
        setContentView(R.layout.activity_absen_ronda);

        initViews();
        setupListeners();




        //Set tanggal hari ini
        TextView Tanggal_absen = findViewById(R.id.Tanggal_absen);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String tanggal = sdf.format(calendar.getTime());
        Tanggal_absen.setText(tanggal);
    }

    // ---------------- PBO: method terpisah ----------------

    /** Ambil semua view dari XML */
    private void initViews() {
        btnBackAbsen       = findViewById(R.id.btnBackAbsen);
        btnKirimAbsen = findViewById(R.id.btnUploadLaporan);
        insert_absenID= findViewById(R.id.insert_absenID);
    }

    /** Listener tombol back & upload */
    private void setupListeners() {
        // Back ke halaman sebelumnya (DashboardActivity) dengan stack Android biasa
        btnBackAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // cukup finish, otomatis balik ke Dashboard
            }
        });

        btnKirimAbsen.setOnClickListener(v -> {

            String id_jadwal = insert_absenID.getText().toString().trim();

            if (id_jadwal.isEmpty()) {
                Toast.makeText(this, "ID Jadwal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            kirimAbsen(id_jadwal);   // <<== Kirim nilai yang user masukkan
        });

    }

    private void kirimAbsen(String id_jadwal) {

        String url = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/insert_absen.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("ABSEN_RESPONSE", response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        boolean success = obj.getBoolean("success");

                        if (success) {
                            Toast.makeText(this, "Absen berhasil dikirim!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, obj.getString("Absen gagal, masukkan ID Jadwal yang benar!"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("ABSEN_PARSE_ERROR", e.toString());
                        Toast.makeText(this, "Absen gagal, masukkan ID Jadwal yang benar!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMsg;

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMsg = new String(error.networkResponse.data);
                    } else {
                        errorMsg = error.toString();
                    }

                    Log.e("ABSEN_ERROR", errorMsg);
                    Toast.makeText(this, "Gagal mengirim absen!", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_jadwal", id_jadwal);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }



}
