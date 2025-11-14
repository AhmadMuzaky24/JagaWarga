package com.example.jagawarga;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class TukarJadwalActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText etJadwalSaya, etJadwalTujuan;
    private Button btnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tukar_jadwal);

        // Inisialisasi View
        btnBack = findViewById(R.id.btnBack);
        etJadwalSaya = findViewById(R.id.etJadwalSaya);
        etJadwalTujuan = findViewById(R.id.etJadwalTujuan);
        btnRequest = findViewById(R.id.btnRequest);

        // Tombol kembali ke activity sebelumnya
        btnBack.setOnClickListener(v -> onBackPressed());

        // Tombol "Request Tukar"
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String jadwalSaya = etJadwalSaya.getText() != null ? etJadwalSaya.getText().toString().trim() : "";
                String jadwalTujuan = etJadwalTujuan.getText() != null ? etJadwalTujuan.getText().toString().trim() : "";

                if (jadwalSaya.isEmpty()) {
                    etJadwalSaya.setError("Harap isi ID Jadwal Saya");
                    return;
                }

                if (jadwalTujuan.isEmpty()) {
                    etJadwalTujuan.setError("Harap isi ID Jadwal Tujuan");
                    return;
                }

                // Contoh aksi (bisa diganti dengan logic API atau Firebase)
                Toast.makeText(TukarJadwalActivity.this,
                        "Request Tukar dikirim:\nDari " + jadwalSaya + " ke " + jadwalTujuan,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
