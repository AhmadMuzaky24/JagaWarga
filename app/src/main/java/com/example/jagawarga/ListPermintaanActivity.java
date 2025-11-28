package com.example.jagawarga;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ListPermintaanActivity extends AppCompatActivity {

    private LinearLayout containerList;
    private ImageButton btnBack;
    private Button btnTabAbsensi, btnTabRegister;

    // --- URL API REGISTER ---
    private String URL_GET_PENDING_REG = "https://newsletter-cod-jeff-cement.trycloudflare.com/jagawarga/get_pending_users.php";
    private String URL_VALIDATE_REG    = "https://newsletter-cod-jeff-cement.trycloudflare.com/jagawarga/validate_user.php";

    // --- URL API ABSENSI (BARU) ---
    private String URL_GET_PENDING_ABSEN = "https://newsletter-cod-jeff-cement.trycloudflare.com/jagawarga/get_pending_absen.php";
    private String URL_VALIDATE_ABSEN    = "https://newsletter-cod-jeff-cement.trycloudflare.com/jagawarga/validate_absen.php";

    private String currentIdRt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_permintaan_register); // Pastikan nama layout XML benar

        // Init Views
        btnBack = findViewById(R.id.btnBack);
        containerList = findViewById(R.id.containerList);
        btnTabAbsensi = findViewById(R.id.btnTabAbsensi);
        btnTabRegister = findViewById(R.id.btnTabRegister);

        // Ambil ID RT
        currentIdRt = PrefUtils.getIdRt(this);

        btnBack.setOnClickListener(v -> finish());

        // --- SETUP TABS ---

        // 1. Klik Tab Register
        btnTabRegister.setOnClickListener(v -> {
            updateTabUI(true); // true = Register aktif
            if (currentIdRt != null) loadPendingRegister(currentIdRt);
        });

        // 2. Klik Tab Absensi
        btnTabAbsensi.setOnClickListener(v -> {
            updateTabUI(false); // false = Absensi aktif
            if (currentIdRt != null) loadPendingAbsen(currentIdRt);
        });

        // Default Load pertama kali: Register
        updateTabUI(true);
        if (currentIdRt != null) loadPendingRegister(currentIdRt);
    }

    // Ubah warna tombol tab agar user tau mana yang aktif
    private void updateTabUI(boolean isRegisterActive) {
        if (isRegisterActive) {
            // Tab Register Putih, Absen Transparan
            btnTabRegister.setBackgroundResource(R.drawable.rounded_button_white);
            btnTabRegister.setTextColor(ContextCompat.getColor(this, R.color.black));

            btnTabAbsensi.setBackgroundResource(android.R.color.transparent);
            btnTabAbsensi.setTextColor(ContextCompat.getColor(this, R.color.gray));
        } else {
            // Tab Absen Putih, Register Transparan
            btnTabAbsensi.setBackgroundResource(R.drawable.rounded_button_white);
            btnTabAbsensi.setTextColor(ContextCompat.getColor(this, R.color.black));

            btnTabRegister.setBackgroundResource(android.R.color.transparent);
            btnTabRegister.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }
        // Bersihkan list saat pindah tab
        containerList.removeAllViews();
    }

    // =================================================================
    // LOGIC PERMINTAAN REGISTER
    // =================================================================
    private void loadPendingRegister(String idRt) {
        String url = URL_GET_PENDING_REG + "?id_rt=" + idRt;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            JSONArray array = object.getJSONArray("data");
                            containerList.removeAllViews(); // Clear

                            if (array.length() == 0) {
                                Toast.makeText(this, "Tidak ada register baru", Toast.LENGTH_SHORT).show();
                            }

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject user = array.getJSONObject(i);
                                addItemRegister(
                                        user.getString("id_warga"),
                                        user.getString("nama"),
                                        user.getString("telepon")
                                );
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(this, "Gagal memuat register", Toast.LENGTH_SHORT).show()
        );
        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }

    private void addItemRegister(String idWarga, String nama, String telp) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_request_register, containerList, false);

        TextView tvNama = itemView.findViewById(R.id.tvNamaWarga);
        TextView tvTelp = itemView.findViewById(R.id.tvTeleponWarga);
        Button btnAcc = itemView.findViewById(R.id.btnAcc);
        Button btnReject = itemView.findViewById(R.id.btnReject);

        tvNama.setText(nama);
        tvTelp.setText(telp);

        btnAcc.setOnClickListener(v -> processValidation(URL_VALIDATE_REG, "id_warga", idWarga, "accept", itemView));
        btnReject.setOnClickListener(v -> processValidation(URL_VALIDATE_REG, "id_warga", idWarga, "reject", itemView));

        containerList.addView(itemView);
    }

    // =================================================================
    // LOGIC PERMINTAAN ABSENSI (NEW)
    // =================================================================
    private void loadPendingAbsen(String idRt) {
        String url = URL_GET_PENDING_ABSEN + "?id_rt=" + idRt;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            JSONArray array = object.getJSONArray("data");
                            containerList.removeAllViews(); // Clear

                            if (array.length() == 0) {
                                Toast.makeText(this, "Tidak ada absen pending", Toast.LENGTH_SHORT).show();
                            }

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject item = array.getJSONObject(i);
                                addItemAbsen(
                                        item.getString("id_absen"),
                                        item.getString("nama_warga"),
                                        item.getString("waktu")
                                );
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(this, "Gagal memuat absen", Toast.LENGTH_SHORT).show()
        );
        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }

    private void addItemAbsen(String idAbsen, String nama, String waktu) {
        // Gunakan layout baru: item_request_absen
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_request_absen, containerList, false);

        TextView tvNama = itemView.findViewById(R.id.tvNamaWargaAbsen);
        TextView tvWaktu = itemView.findViewById(R.id.tvWaktuAbsen);
        Button btnAcc = itemView.findViewById(R.id.btnAccAbsen);
        Button btnReject = itemView.findViewById(R.id.btnRejectAbsen);

        tvNama.setText(nama);
        tvWaktu.setText("Pukul: " + waktu);

        // Panggil API Validasi Absen
        // Param key di PHP validate_absen adalah 'id_absen'
        btnAcc.setOnClickListener(v -> processValidation(URL_VALIDATE_ABSEN, "id_absen", idAbsen, "accept", itemView));
        btnReject.setOnClickListener(v -> processValidation(URL_VALIDATE_ABSEN, "id_absen", idAbsen, "reject", itemView));

        containerList.addView(itemView);
    }

    // =================================================================
    // METHOD VALIDASI REUSABLE (Bisa untuk Register & Absen)
    // =================================================================
    private void processValidation(String url, String idKey, String idValue, String action, View itemView) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Berhasil: " + action, Toast.LENGTH_SHORT).show();
                            containerList.removeView(itemView); // Hapus dari layar
                        } else {
                            Toast.makeText(this, "Gagal: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(this, "Koneksi Error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(idKey, idValue); // id_warga atau id_absen
                params.put("action", action);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}