package com.example.jagawarga;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class TerimaLaporanActivity extends AppCompatActivity {

    private RecyclerView rvLaporan;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    // GANTI DENGAN URL CLOUDFLARE/IP KAMU
    private String URL_GET_LAPORAN = "https://newsletter-cod-jeff-cement.trycloudflare.com/jagawarga/get_laporan_masuk.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terima_laporan);

        // Inisialisasi View
        rvLaporan = findViewById(R.id.rvLaporanMasuk);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        rvLaporan.setLayoutManager(new LinearLayoutManager(this));

        // Tombol Kembali
        btnBack.setOnClickListener(v -> finish());

        // Load Data
        loadLaporan();
    }

    private void loadLaporan() {
        String idRt = PrefUtils.getIdRt(this);
        if (idRt == null) {
            Toast.makeText(this, "ID RT tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String url = URL_GET_LAPORAN + "?id_rt=" + idRt;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    Log.d("DEBUG_LAPORAN", "Response: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONArray data = obj.getJSONArray("data");

                            // Cek jika data kosong
                            if (data.length() == 0) {
                                Toast.makeText(this, "Belum ada laporan masuk.", Toast.LENGTH_SHORT).show();
                            }

                            // Pasang Adapter
                            LaporanAdapter adapter = new LaporanAdapter(data);
                            rvLaporan.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "Gagal: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Format data salah", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String msg = "Gagal koneksi server";
                    if(error.networkResponse != null) {
                        msg += " (Code: " + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    Log.e("DEBUG_LAPORAN", "Error: " + error.toString());
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    // === ADAPTER ===
    class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.Holder> {
        JSONArray data;

        public LaporanAdapter(JSONArray data) {
            this.data = data;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laporan_masuk, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            try {
                JSONObject item = data.getJSONObject(position);

                // 1. Set Jenis Laporan
                holder.tvJenis.setText(item.optString("jenis_laporan", "Laporan"));

                // 2. Set Nama Pelapor
                holder.tvNama.setText("Oleh: " + item.optString("nama_pelapor", "Warga"));

                // 3. Set Isi Laporan (FIX: Ganti 'deskripsi' jadi 'isi_laporan')
                holder.tvDeskripsi.setText(item.optString("isi_laporan", "-"));

                // 4. Set Waktu
                if(item.has("waktu_fmt")) {
                    holder.tvWaktu.setText(item.getString("waktu_fmt"));
                } else {
                    holder.tvWaktu.setText(item.optString("waktu_laporan"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return data.length();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView tvJenis, tvNama, tvDeskripsi, tvWaktu;
            // Status dihapus dari ViewHolder

            public Holder(@NonNull View itemView) {
                super(itemView);
                tvJenis = itemView.findViewById(R.id.tvJenisLaporan);
                tvNama = itemView.findViewById(R.id.tvNamaPelapor);
                tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
                tvWaktu = itemView.findViewById(R.id.tvWaktu);
            }
        }
    }
}