package com.example.jagawarga;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AbsenRondaActivity extends AppCompatActivity {

    private ImageButton btnBackAbsen;
    private Button btnKirimAbsen;
    private EditText insert_absenID;
    private TextView textTanggalAbsen;

    // URL API (Pastikan sudah benar)
    private static final String URL_INSERT_ABSEN = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/insert_absen.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_ronda);

        initViews();
        setupDate();

        btnBackAbsen.setOnClickListener(v -> finish());

        btnKirimAbsen.setOnClickListener(v -> {
            String idJadwal = insert_absenID.getText().toString().trim();
            if (idJadwal.isEmpty()) {
                Toast.makeText(this, "ID Jadwal tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            // SECURITY: Ambil ID Warga dari session login
            String idWarga = PrefUtils.getIdWarga(this);
            if (idWarga != null) {
                kirimAbsen(idJadwal, idWarga); // Kirim ID Warga juga
            }
        });
    }

    private void initViews() {
        btnBackAbsen = findViewById(R.id.btnBackAbsen);
        btnKirimAbsen = findViewById(R.id.btnUploadLaporan);
        insert_absenID = findViewById(R.id.insert_absenID);
        textTanggalAbsen = findViewById(R.id.Tanggal_absen);
    }

    private void setupDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        textTanggalAbsen.setText(sdf.format(calendar.getTime()));
    }

    // --- LOGIC KIRIM ABSEN DENGAN VERIFIKASI USER ---
    private void kirimAbsen(String idJadwal, String idWarga) {
        ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Memverifikasi jadwal...");
        loading.setCancelable(false);
        loading.show();

        StringRequest request = new StringRequest(Request.Method.POST, URL_INSERT_ABSEN,
                response -> {
                    loading.dismiss();
                    Log.d("ABSEN_RESP", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            insert_absenID.setText("");
                            finish(); // Tutup activity setelah sukses
                        } else {
                            // Pesan error dari server (misal: "Bukan jadwal Anda")
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error respon server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loading.dismiss();
                    Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_jadwal", idJadwal);
                params.put("id_warga", idWarga); // <-- Kirim ID Pelaku ke server
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}