package com.example.jagawarga;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class uji_coba_generate_jadwal extends AppCompatActivity {

    Button btnGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uji_coba_generate_jadwal);

        btnGenerate = findViewById(R.id.btnGenerate);

        // AMBIL id_rt menggunakan method reusable
        String id_rt = PrefUtils.getIdRt(this);

        if (id_rt == null) {
            return; // hentikan proses jika id_rt tidak ditemukan
        }

        btnGenerate.setOnClickListener(v -> generateJadwal(id_rt));
    }

    private void generateJadwal(String id_rt) {
        String url = "https://hat-making-margaret-favor.trycloudflare.com/jagawarga/generate_jadwal.php";

        ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Generating jadwal...");
        loading.show();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    loading.dismiss();
                    Log.d("API_GENERATE", "Raw Response: " + response);

                    try {
                        JSONObject json = new JSONObject(response);
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                },
                error -> {
                    loading.dismiss();
                    Toast.makeText(this,
                            "Gagal terhubung ke server",
                            Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_rt", id_rt);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}


