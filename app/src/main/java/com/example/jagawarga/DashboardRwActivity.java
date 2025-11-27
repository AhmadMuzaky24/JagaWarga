package com.example.jagawarga;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardRwActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_rw); // Pastikan layout ini ada di res/layout

        // Greeting Nama RW
        String namaRw = getIntent().getStringExtra("nama_user");
        if (namaRw == null) namaRw = "Pak RW";

        TextView tvGreeting = findViewById(R.id.tvGreeting); // Sesuaikan ID di layout RW Anda
        if (tvGreeting != null) {
            tvGreeting.setText("Hai, " + namaRw + " !");
        }
    }
}