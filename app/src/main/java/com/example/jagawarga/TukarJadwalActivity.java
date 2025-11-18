package com.example.jagawarga;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class TukarJadwalActivity extends AppCompatActivity {

    private ImageButton btnBackAbsen;
    private Button btnTukarAbsen;
    private EditText inputIdJadwalSaya, inputIdJadwalTujuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tukar_jadwal);

        btnBackAbsen = findViewById(R.id.btnBackAbsen);
        btnTukarAbsen = findViewById(R.id.btnTukarAbsen);
        inputIdJadwalSaya = findViewById(R.id.inputIdJadwalSaya);
        inputIdJadwalTujuan = findViewById(R.id.inputIdJadwalTujuan);

        btnBackAbsen.setOnClickListener(v -> onBackPressed());

        // nanti logic tukar jadwal taruh di sini
        btnTukarAbsen.setOnClickListener(v -> {
            String jadwalSaya = inputIdJadwalSaya.getText().toString().trim();
            String jadwalTujuan = inputIdJadwalTujuan.getText().toString().trim();
            // TODO: proses tukar jadwal di sini
        });
    }
}
