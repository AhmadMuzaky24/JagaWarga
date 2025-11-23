package com.example.jagawarga;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class JadwalRondaActivity extends AppCompatActivity {

    private ImageButton btnBackJadwal, btnPrevDate, btnNextDate;
    private Button btnKembaliJadwal;
    private TextView textTanggalPilihan;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_ronda);

        initViews();
        initDate();
        setupListeners();
    }

    private void initViews() {
        btnBackJadwal = findViewById(R.id.btnBackJadwal);
        btnPrevDate   = findViewById(R.id.btnPrevDate);
        btnNextDate   = findViewById(R.id.btnNextDate);
        btnKembaliJadwal = findViewById(R.id.btnKembaliJadwal);
        textTanggalPilihan = findViewById(R.id.textTanggalPilihan);
    }

    private void initDate() {
        calendar = Calendar.getInstance();
        // format: "Senin, 24 November" (locale Indonesia)
        dateFormat = new SimpleDateFormat("EEEE, dd MMMM", new Locale("id", "ID"));
        updateDateText();
    }

    private void setupListeners() {
        View.OnClickListener backListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // kembali ke Dashboard
            }
        };

        btnBackJadwal.setOnClickListener(backListener);
        btnKembaliJadwal.setOnClickListener(backListener);

        btnPrevDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                updateDateText();
            }
        });

        btnNextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                updateDateText();
            }
        });
    }

    private void updateDateText() {
        String formatted = dateFormat.format(calendar.getTime());
        // huruf kapital awal hari (opsional)
        formatted = capitalizeFirst(formatted);
        textTanggalPilihan.setText(formatted);
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0,1).toUpperCase() + text.substring(1);
    }
}
