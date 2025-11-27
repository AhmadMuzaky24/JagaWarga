package com.example.jagawarga;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    // --- UI Components ---
    private ImageButton btnBackAbsen;
    private Button btnKirimAbsen;
    private EditText insert_absenID;
    private TextView textTanggalAbsen;

    // --- URL API ---
    // Pastikan URL ini sesuai dengan Cloudflare Tunnel kamu yang aktif
    // Dan pastikan file insert_absen.php sudah dibuat di server
    private static final String URL_INSERT_ABSEN = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/insert_absen.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pastikan layout XML-nya benar (activity_absen_ronda)
        setContentView(R.layout.activity_absen_ronda);

        initViews();
        setupDate();
        setupListeners();
    }

    // --- 1. Inisialisasi View ---
    private void initViews() {
        btnBackAbsen = findViewById(R.id.btnBackAbsen);
        // Sesuaikan ID tombol kirim dengan di XML (btnUploadLaporan)
        btnKirimAbsen = findViewById(R.id.btnUploadLaporan);
        insert_absenID = findViewById(R.id.insert_absenID);
        textTanggalAbsen = findViewById(R.id.Tanggal_absen);
    }

    // --- 2. Setup Tanggal Hari Ini ---
    private void setupDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String tanggal = sdf.format(calendar.getTime());
        textTanggalAbsen.setText(tanggal);
    }

    // --- 3. Setup Listener Tombol ---
    private void setupListeners() {
        // Tombol Back
        btnBackAbsen.setOnClickListener(v -> finish());

        // Tombol Kirim Absen
        btnKirimAbsen.setOnClickListener(v -> {
            String idJadwal = insert_absenID.getText().toString().trim();

            if (idJadwal.isEmpty()) {
                Toast.makeText(this, "ID Jadwal tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            kirimAbsen(idJadwal);
        });
    }

    // --- 4. Logic Kirim Data ke Server ---
    private void kirimAbsen(String idJadwal) {

        // Tampilkan Loading agar User Menunggu
        ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mengirim permintaan absen...");
        loading.setCancelable(false); // Tidak bisa di-cancel user
        loading.show();

        StringRequest request = new StringRequest(Request.Method.POST, URL_INSERT_ABSEN,
                response -> {
                    loading.dismiss(); // Hilangkan loading
                    Log.d("ABSEN_RESPONSE", response);

                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");

                        if (success) {
                            // Sukses: Tampilkan pesan dan kosongkan input
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                            insert_absenID.setText("");

                            // Opsional: Jika ingin langsung keluar setelah absen berhasil
                            // finish();
                        } else {
                            // Gagal dari logic PHP (misal ID Jadwal salah)
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("ABSEN_PARSE", e.toString());
                        Toast.makeText(this, "Gagal memproses respon server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loading.dismiss(); // Hilangkan loading
                    Log.e("ABSEN_NETWORK", error.toString());
                    Toast.makeText(this, "Gagal terhubung ke server. Cek koneksi internet!", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Parameter 'id_jadwal' ini akan ditangkap oleh $_POST['id_jadwal'] di PHP
                params.put("id_jadwal", idJadwal);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}