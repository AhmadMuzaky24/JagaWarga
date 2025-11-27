package com.example.jagawarga;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TukarJadwalActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etIdJadwalSaya;
    private EditText etIdJadwalTujuan;
    private Button btnTukarJadwal;

    // GANTI dengan URL server kamu
    private static final String SWAP_JADWAL_URL = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/swap_jadwal.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tukar_jadwal);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBackAbsen);
        etIdJadwalSaya = findViewById(R.id.inputIdJadwalSaya);
        etIdJadwalTujuan = findViewById(R.id.inputIdJadwalTujuan);
        btnTukarJadwal = findViewById(R.id.btnTukarAbsen);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnTukarJadwal.setOnClickListener(v -> handleTukarJadwal());
    }

    private void handleTukarJadwal() {
        // pakai Prefutils punyamu
        String idRtUser = PrefUtils.getIdRt(this);
        if (idRtUser == null || idRtUser.isEmpty()) {
            // Prefutils sudah menampilkan Toast sendiri kalau null,
            // jadi di sini cukup stop saja
            return;
        }

        String idSayaStr = etIdJadwalSaya.getText().toString().trim();
        String idTujuanStr = etIdJadwalTujuan.getText().toString().trim();

        if (idSayaStr.isEmpty()) {
            etIdJadwalSaya.setError("ID Jadwal Saya wajib diisi");
            etIdJadwalSaya.requestFocus();
            return;
        }
        if (idTujuanStr.isEmpty()) {
            etIdJadwalTujuan.setError("ID Jadwal Tujuan wajib diisi");
            etIdJadwalTujuan.requestFocus();
            return;
        }

        int idSaya, idTujuan;
        try {
            idSaya = Integer.parseInt(idSayaStr);
            idTujuan = Integer.parseInt(idTujuanStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID jadwal harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }

        callSwapJadwalApi(idSaya, idTujuan, idRtUser);
    }

    private void callSwapJadwalApi(int idSaya, int idTujuan, String idRtUser) {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                SWAP_JADWAL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("SWAP_RAW", "Response dari server: " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean success = json.optBoolean("success", false);
                            String message = json.optString("message", "Terjadi kesalahan");

                            Toast.makeText(TukarJadwalActivity.this,
                                    message, Toast.LENGTH_LONG).show();

                            if (success) {
                                etIdJadwalSaya.setText("");
                                etIdJadwalTujuan.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TukarJadwalActivity.this,
                                    "Response tidak valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(TukarJadwalActivity.this,
                                "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_jadwal_saya", String.valueOf(idSaya));
                params.put("id_jadwal_tujuan", String.valueOf(idTujuan));
                params.put("id_rt_user", idRtUser);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
