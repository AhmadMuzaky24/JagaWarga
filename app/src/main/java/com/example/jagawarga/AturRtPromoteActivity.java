package com.example.jagawarga;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AturRtPromoteActivity extends AppCompatActivity {

    private EditText etIdWarga, etTelepon;
    private Button btnAction;
    private ImageButton btnBack;
    private TextView tabPromosikan, tabTurunkan;

    private static final String BASE_URL = "https://newsletter-cod-jeff-cement.trycloudflare.com/jagawarga/";
    private static final String ATUR_RT_URL = BASE_URL + "atur_rt.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote_rt);   // ⬅️ layout PROMOSIKAN

        initViews();
        setupTabs();
        setupButton();
        setupBackButton();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        etIdWarga   = findViewById(R.id.inputIDWarga);
        etTelepon   = findViewById(R.id.inputPhoneWarga);
        btnAction   = findViewById(R.id.btnActionPromote);

        tabPromosikan = findViewById(R.id.btnTabPromote);
        tabTurunkan   = findViewById(R.id.btnTabRevoke);

        btnAction.setText("Promosikan");
    }

    private void setupTabs() {
        // Tab Promosikan → posisi sekarang, tidak pindah
        tabPromosikan.setOnClickListener(v -> {
        });

        // Tab Turunkan → pindah ke activity Turunkan
        tabTurunkan.setOnClickListener(v -> {
            startActivity(new android.content.Intent(
                    AturRtPromoteActivity.this,
                    AturRtRevokeActivity.class
            ));
            finish(); // supaya back tidak bolak-balik
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setupButton() {
        btnAction.setOnClickListener(v -> {
            String idWargaStr = etIdWarga.getText().toString().trim();
            String telepon    = etTelepon.getText().toString().trim();

            if (idWargaStr.isEmpty()) {
                etIdWarga.setError("ID Warga wajib diisi");
                etIdWarga.requestFocus();
                return;
            }

            int idWarga;
            try {
                idWarga = Integer.parseInt(idWargaStr);
            } catch (NumberFormatException e) {
                etIdWarga.setError("ID Warga harus berupa angka");
                etIdWarga.requestFocus();
                return;
            }

            if (telepon.isEmpty()) {
                etTelepon.setError("No. telepon wajib diisi");
                etTelepon.requestFocus();
                return;
            }

            callAturRtApi("promosikan", idWarga, telepon);
        });
    }

    private void callAturRtApi(String mode, int idWarga, String telepon) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                ATUR_RT_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);
                        String message = json.optString("message", "Terjadi kesalahan");
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                        if (success) {
                            etIdWarga.setText("");
                            etTelepon.setText("");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response tidak valid", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("mode", mode);                          // "promosikan"
                p.put("id_warga", String.valueOf(idWarga));
                p.put("telepon", telepon);
                return p;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }
}
