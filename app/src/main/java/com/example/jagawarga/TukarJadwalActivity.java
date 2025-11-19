package com.example.jagawarga;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class TukarJadwalActivity extends AppCompatActivity {

    private ImageButton btnBackAbsen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tukar_jadwal);

        btnBackAbsen = findViewById(R.id.btnBackAbsen);

        btnBackAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Balik ke Dashboard
                finish();
            }
        });
    }
}
