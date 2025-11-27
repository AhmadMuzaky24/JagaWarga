package com.example.jagawarga;

import android.app.ProgressDialog;
import android.os.Bundle;
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

    // Ganti URL dengan Cloudflare kamu
    private String URL_CREATE = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/create_pengumuman.php";

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
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Berhasil diterbitkan!", Toast.LENGTH_LONG).show();
                            finish(); // Kembali ke dashboard
                        } else {
                            Toast.makeText(this, "Gagal: " + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> {
                    pd.dismiss();
                    Toast.makeText(this, "Gagal koneksi server", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("judul", judul);
                params.put("isi", isi);
                params.put("id_rt", myIdRt); // ID RT dikirim otomatis
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}