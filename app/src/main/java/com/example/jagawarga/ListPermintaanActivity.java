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

    // Ganti dengan URL Cloudflare kamu
    private String URL_GET_PENDING = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/get_pending_users.php";
    private String URL_VALIDATE    = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/validate_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_permintaan_register);

        btnBack = findViewById(R.id.btnBack);
        containerList = findViewById(R.id.containerList);

        btnBack.setOnClickListener(v -> finish());

        // Ambil ID RT dari sesi login (pakai PrefUtils yang sudah kamu punya)
        String idRt = PrefUtils.getIdRt(this);
        if (idRt != null) {
            loadPendingUsers(idRt);
        }
    }

    private void loadPendingUsers(String idRt) {
        // Tambahkan parameter id_rt ke URL
        String url = URL_GET_PENDING + "?id_rt=" + idRt;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            JSONArray array = object.getJSONArray("data");

                            // Bersihkan list lama sebelum mengisi baru
                            containerList.removeAllViews();

                            if (array.length() == 0) {
                                Toast.makeText(this, "Tidak ada permintaan baru", Toast.LENGTH_SHORT).show();
                            }

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject user = array.getJSONObject(i);
                                String idWarga = user.getString("id_warga");
                                String nama = user.getString("nama");
                                String telp = user.getString("telepon");

                                // Tambahkan item ke tampilan
                                addItemToLayout(idWarga, nama, telp);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void addItemToLayout(String idWarga, String nama, String telp) {
        // Inflate layout item_request_register.xml
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_request_register, containerList, false);

        TextView tvNama = itemView.findViewById(R.id.tvNamaWarga);
        TextView tvTelp = itemView.findViewById(R.id.tvTeleponWarga);
        Button btnAcc = itemView.findViewById(R.id.btnAcc);
        Button btnReject = itemView.findViewById(R.id.btnReject);

        tvNama.setText(nama);
        tvTelp.setText(telp);

        // Logic Tombol Validasi (Terima)
        btnAcc.setOnClickListener(v -> {
            processValidation(idWarga, "accept", itemView);
        });

        // Logic Tombol Reject (Tolak)
        btnReject.setOnClickListener(v -> {
            processValidation(idWarga, "reject", itemView);
        });

        // Masukkan item ke container
        containerList.addView(itemView);
    }

    private void processValidation(String idWarga, String action, View itemView) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_VALIDATE,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, action.equals("accept") ? "User Divalidasi!" : "User Ditolak", Toast.LENGTH_SHORT).show();
                            // Hapus item dari layar secara animasi/langsung
                            containerList.removeView(itemView);
                        } else {
                            Toast.makeText(this, "Gagal: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Koneksi Error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_warga", idWarga);
                params.put("action", action);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}