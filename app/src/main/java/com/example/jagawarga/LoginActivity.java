package com.example.jagawarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // Container Views
    private ViewGroup mainContainer;
    private LinearLayout layoutLogin, layoutRegister;

    // Tab Buttons
    private Button btnMasukTab, btnDaftarTab;

    // Login Fields
    private EditText inputPhoneLogin, inputPasswordLogin;
    private ImageView btnTogglePassLogin;
    private Button btnLogin;
    private TextView textForgot;

    // Register Fields
    private EditText inputNamaReg, inputPhoneReg, inputPassReg;
    private Spinner inputRtReg;
    private ImageView btnTogglePassReg;
    private Button btnRegisterAction;

    // State
    private boolean isLoginPassVisible = false;
    private boolean isRegPassVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Cek apakah user sudah login sebelumnya (Auto Login)
        // checkSession();

        initViews();
        setupSpinnerRt();
        setupTabs();
        setupPasswordToggles();
        setupActionButtons();
    }

    private void checkSession() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String savedId = prefs.getString("id", null);
        String savedRole = prefs.getString("role", "Warga");

        if (savedId != null) {
            redirectDashboard(savedRole, prefs.getString("nama", "User"));
        }
    }

    private void initViews() {
        mainContainer = findViewById(R.id.mainContainer);
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutRegister = findViewById(R.id.layoutRegister);

        btnMasukTab = findViewById(R.id.btnMasukTab);
        btnDaftarTab = findViewById(R.id.btnDaftarTab);

        inputPhoneLogin = findViewById(R.id.inputPhoneLogin);
        inputPasswordLogin = findViewById(R.id.inputPasswordLogin);
        btnTogglePassLogin = findViewById(R.id.btnTogglePassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        textForgot = findViewById(R.id.textForgot);

        inputNamaReg = findViewById(R.id.inputNamaReg);
        inputPhoneReg = findViewById(R.id.inputPhoneReg);
        inputRtReg = findViewById(R.id.inputRtReg);
        inputPassReg = findViewById(R.id.inputPassReg);
        btnTogglePassReg = findViewById(R.id.btnTogglePassReg);
        btnRegisterAction = findViewById(R.id.btnRegisterAction);
    }

    private void setupSpinnerRt() {
        String[] rtOptions = {"01", "02", "03", "04"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rtOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputRtReg.setAdapter(adapter);
    }

    private void setupTabs() {
        btnMasukTab.setOnClickListener(v -> {
            if (layoutLogin.getVisibility() == View.VISIBLE) return;
            TransitionManager.beginDelayedTransition(mainContainer);
            layoutRegister.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
            updateTabStyle(true);
        });

        btnDaftarTab.setOnClickListener(v -> {
            if (layoutRegister.getVisibility() == View.VISIBLE) return;
            TransitionManager.beginDelayedTransition(mainContainer);
            layoutLogin.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.VISIBLE);
            updateTabStyle(false);
        });
    }

    private void updateTabStyle(boolean isLoginActive) {
        if (isLoginActive) {
            btnMasukTab.setBackgroundResource(R.drawable.rounded_button);
            btnMasukTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnMasukTab.setTextColor(Color.BLACK);
            btnDaftarTab.setBackgroundColor(Color.TRANSPARENT);
            btnDaftarTab.setTextColor(ContextCompat.getColor(this, R.color.gray));
        } else {
            btnDaftarTab.setBackgroundResource(R.drawable.rounded_button);
            btnDaftarTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnDaftarTab.setTextColor(Color.BLACK);
            btnMasukTab.setBackgroundColor(Color.TRANSPARENT);
            btnMasukTab.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }
    }

    private void setupPasswordToggles() {
        btnTogglePassLogin.setOnClickListener(v -> isLoginPassVisible = togglePassword(inputPasswordLogin, btnTogglePassLogin, isLoginPassVisible));
        btnTogglePassReg.setOnClickListener(v -> isRegPassVisible = togglePassword(inputPassReg, btnTogglePassReg, isRegPassVisible));
    }

    private boolean togglePassword(EditText input, ImageView icon, boolean isVisible) {
        if (isVisible) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.icon_mata2);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.icon_mata1);
        }
        input.setSelection(input.getText().length());
        return !isVisible;
    }

    private void setupActionButtons() {
        btnLogin.setOnClickListener(v -> {
            String phone = inputPhoneLogin.getText().toString().trim();
            String pass = inputPasswordLogin.getText().toString().trim();
            if (phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Isi nomor telepon dan password!", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(phone, pass);
            }
        });

        btnRegisterAction.setOnClickListener(v -> performRegister());

        textForgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }

    // --- LOGIC LOGIN UTAMA YANG DIPERBAIKI ---
    private void performLogin(String telepon, String password) {
        // Pastikan URL ini benar sesuai Cloudflare Tunnel kamu
        String url = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/login.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API_LOGIN", "Response Raw: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        // Periksa apakah field 'success' ada dan true
                        if (obj.has("success") && obj.getBoolean("success")) {
                            JSONObject user = obj.getJSONObject("data");

                            // 1. Ambil Data dengan Aman (Gunakan optString agar tidak Crash)
                            // Jika data null di JSON, dia akan pakai nilai default parameter kedua
                            String nama = user.optString("nama", "Warga");
                            String id_warga = user.optString("id", "0");
                            String id_rt = user.optString("id_rt", "0");
                            String role = user.optString("role", "Warga");
                            String telp = user.optString("telepon", "");

                            // 2. Simpan ke SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("id", id_warga)
                                    .putString("id_rt", id_rt)
                                    .putString("nama", nama)
                                    .putString("role", role)
                                    .putString("telepon", telp)
                                    .apply();

                            Toast.makeText(this, "Login Berhasil sebagai " + role, Toast.LENGTH_SHORT).show();

                            // 3. Panggil fungsi redirect
                            redirectDashboard(role, nama);

                        } else {
                            // Jika login gagal (password salah dll)
                            String msg = obj.optString("message", "Login Gagal");
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("LOGIN_PARSE_ERROR", "Error: " + e.getMessage());
                        Toast.makeText(this, "Format data dari server salah!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LOGIN_NETWORK_ERROR", "Error: " + error.toString());
                    Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("telepon", telepon);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    // Fungsi Terpisah untuk Mengatur Arah Dashboard
    private void redirectDashboard(String role, String namaUser) {
        Intent intent;

        // Normalisasi string role (antisipasi huruf besar/kecil dari database)
        String roleLower = role.toLowerCase();

        if (roleLower.contains("KetuaRT")) {
            // Role mengandung kata 'rt' (misal: "KetuaRT", "admin_rt", "RT")
            intent = new Intent(LoginActivity.this, DashboardRtActivity.class);
        } else if (roleLower.contains("KetuaRW")) {
            // Role mengandung kata 'rw'
            intent = new Intent(LoginActivity.this, DashboardRwActivity.class);
        } else {
            // Default ke Warga
            intent = new Intent(LoginActivity.this, DashboardActivity.class);
        }

        // Kirim nama user (opsional, karena di Dashboard sudah ambil dari Prefs)
        intent.putExtra("nama_user", namaUser);

        // Mulai Activity dan hapus LoginActivity dari stack (biar gak bisa di-back)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void performRegister() {
        String nama = inputNamaReg.getText().toString();
        String telepon = inputPhoneReg.getText().toString();
        String id_rt = "";
        if (inputRtReg.getSelectedItem() != null) {
            id_rt = inputRtReg.getSelectedItem().toString();
        }
        String password = inputPassReg.getText().toString();

        if(nama.isEmpty() || telepon.isEmpty() || id_rt.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/register.php";
        String finalIdRt = id_rt;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API_REG", response);
                    Toast.makeText(this, "Permintaan registrasi dikirim!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, PendingRegisterActivity.class);
                    startActivity(intent);
                },
                error -> Toast.makeText(this, "Gagal Daftar: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("telepon", telepon);
                params.put("id_rt", finalIdRt);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}