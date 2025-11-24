package com.example.jagawarga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    // ===== Menu utama =====
    private LinearLayout menuAbsen;
    private LinearLayout menuTukar;
    private LinearLayout menuLapor;
    private LinearLayout menuJadwal;

    // ===== Card kontak =====
    private LinearLayout tvContactNumber;
    private TextView tvContactNumberText;
    private TextView tvContactLocation;

    // ===== Pengumuman =====
    private TextView tvAnnouncement1Text, tvAnnouncement1Time;
    private TextView tvAnnouncement2Text, tvAnnouncement2Time;
    private TextView tvAnnouncement3Text, tvAnnouncement3Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // ===== Greeting nama user =====
        String namaUser = getIntent().getStringExtra("nama_user");
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (namaUser == null || namaUser.trim().isEmpty()) {
            namaUser = "User";
        }
        tvGreeting.setText("Hai, " + namaUser + " !");

        // ===== Set tanggal hari ini =====
        TextView tanggalCurrent = findViewById(R.id.tanggal_current);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String tanggal = sdf.format(calendar.getTime());
        tanggalCurrent.setText(tanggal);

        // ===== Init & navigasi menu =====
        menuAbsen  = findViewById(R.id.menuAbsen);
        menuTukar  = findViewById(R.id.menuTukar);
        menuLapor  = findViewById(R.id.menuLapor);
        menuJadwal = findViewById(R.id.menuJadwal);

        setupMenuNavigation(menuAbsen,  AbsenRondaActivity.class);
        setupMenuNavigation(menuTukar,  TukarJadwalActivity.class);
        setupMenuNavigation(menuLapor,  LaporanKeamananActivity.class);
        setupMenuNavigation(menuJadwal, JadwalRondaActivity.class);

        // ===== Card kontak =====
        tvContactNumber     = findViewById(R.id.tvContactNumber);
        tvContactNumberText = findViewById(R.id.tvContactNumberText);
        tvContactLocation   = findViewById(R.id.tvContactLocation);
        tvContactLocation = findViewById(R.id.tvContactLocation);

        // isi default (bisa di-update dari API nanti)
        setupContactCard("0813-2424-2626", "RT 01 / Pos 01 (Utara)");

        // ===== Pengumuman =====
        tvAnnouncement1Text = findViewById(R.id.tvAnnouncement1Text);
        tvAnnouncement1Time = findViewById(R.id.tvAnnouncement1Time);
        tvAnnouncement2Text = findViewById(R.id.tvAnnouncement2Text);
        tvAnnouncement2Time = findViewById(R.id.tvAnnouncement2Time);
        tvAnnouncement3Text = findViewById(R.id.tvAnnouncement3Text);
        tvAnnouncement3Time = findViewById(R.id.tvAnnouncement3Time);

        setupAnnouncements();
    }

    // =======================
    //   FUNGSI PBO REUSABLE
    // =======================

    private void setupMenuNavigation(LinearLayout menuView,
                                     final Class<?> targetActivity) {
        if (menuView == null) return;

        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =
                        new Intent(DashboardActivity.this, targetActivity);
                startActivity(intent);
            }
        });
    }

    // Atur isi card kontak dalam satu fungsi
    private void setupContactCard(String phoneNumber, String location) {
        if (tvContactNumber != null) {
            tvContactNumberText.setText(phoneNumber);
            tvContactLocation.setText(location);
        }
        if (tvContactLocation != null) {
            tvContactLocation.setText(location);
        }
        // nanti kalau mau tambah klik ke WhatsApp/Telepon,
        // tinggal tambah 1 fungsi lagi di sini.
    }

    // Representasi 1 pengumuman
    private static class Announcement {
        final String text;
        final String time;

        Announcement(String text, String time) {
            this.text = text;
            this.time = time;
        }
    }

    // Set maksimal 3 pengumuman
    private void setupAnnouncements() {
        Announcement[] data = new Announcement[]{
                new Announcement("Orang mencurigakan di RT 03", "10 m lalu"),
                new Announcement("Pencurian semen di RT 01", "Kemarin"),
                new Announcement("Pengumuman kalender ronda RT 02", "1 bln lalu")
        };

        bindAnnouncement(tvAnnouncement1Text, tvAnnouncement1Time, data[0]);
        bindAnnouncement(tvAnnouncement2Text, tvAnnouncement2Time, data[1]);
        bindAnnouncement(tvAnnouncement3Text, tvAnnouncement3Time, data[2]);
    }

    // Bind 1 item pengumuman
    private void bindAnnouncement(TextView tvText,
                                  TextView tvTime,
                                  Announcement announcement) {
        if (announcement == null) return;
        if (tvText != null) tvText.setText(announcement.text);
        if (tvTime != null) tvTime.setText(announcement.time);
    }
}
