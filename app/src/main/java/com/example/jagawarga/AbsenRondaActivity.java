package com.example.jagawarga;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AbsenRondaActivity extends AppCompatActivity {

    private ImageButton btnBackAbsen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_ronda);

        btnBackAbsen = findViewById(R.id.btnBackAbsen);

        btnBackAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cukup finish supaya balik ke Dashboard
                finish();
            }
        });
    }
}
