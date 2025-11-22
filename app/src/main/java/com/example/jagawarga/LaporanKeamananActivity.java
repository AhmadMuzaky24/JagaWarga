package com.example.jagawarga;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LaporanKeamananActivity extends AppCompatActivity {

    private Spinner spinnerJenisLaporan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_keamanan); // pastikan nama layout benar

        // inisialisasi view
        spinnerJenisLaporan = findViewById(R.id.spinnerJenisLaporan);
        ImageButton btnBack = findViewById(R.id.btnBackLaporan);
        EditText inputDetailLaporan = findViewById(R.id.inputDetailLaporan);

        setupJenisLaporanSpinner();
        setupBackButton(btnBack);

        inputDetailLaporan.setScroller(new Scroller(this));
        inputDetailLaporan.setVerticalScrollBarEnabled(true);
        inputDetailLaporan.setMovementMethod(new ScrollingMovementMethod());
        inputDetailLaporan.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false; // biarkan EditText juga menerima event (ketik)
        });

        // Set tanggal + waktu sekarang
        TextView inputTanggalLaporan = findViewById(R.id.inputTanggalLaporan);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM, HH:mm 'WIB'", new Locale("id", "ID"));

        String tanggalWaktu = sdf.format(calendar.getTime());
        inputTanggalLaporan.setText(tanggalWaktu);
        inputTanggalLaporan.setFocusable(false);
        inputTanggalLaporan.setClickable(false);


    }

    // --- Logic dropdown Jenis Laporan ---
    private void setupJenisLaporanSpinner() {
        String[] jenisLaporan = new String[]{
                "Keributan",
                "Perusakan",
                "Pencurian",
                "Penculikan",
                "Lainnya"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                jenisLaporan
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerJenisLaporan.setAdapter(adapter);
    }

    // --- Logic tombol back ---
    private void setupBackButton(ImageButton btnBack) {
        if (btnBack == null) return;

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // karena activity ini dipanggil dari Dashboard,
                // finish() akan menutup activity ini dan kembali ke DashboardActivity
                finish();
            }
        });
    }
}
