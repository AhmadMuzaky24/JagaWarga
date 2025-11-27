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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private ViewGroup mainContainer; // Root layout untuk animasi
    private LinearLayout layoutLogin, layoutRegister;

    // Tab Buttons
    private Button btnMasukTab, btnDaftarTab;

    // Login Fields
    private EditText inputPhoneLogin, inputPasswordLogin;
    private ImageView btnTogglePassLogin;
    private Button btnLogin;

    // Register Fields
    private EditText inputNamaReg, inputPhoneReg, inputRtReg, inputPassReg;
    private ImageView btnTogglePassReg;
    private Button btnRegisterAction;

    // State
    private boolean isLoginPassVisible = false;
    private boolean isRegPassVisible = false;

    // Lupa Passowrd
    private TextView textForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupTabs();
        setupPasswordToggles();
        setupActionButtons();
    }

    private void initViews() {
        mainContainer = findViewById(R.id.mainContainer);
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutRegister = findViewById(R.id.layoutRegister);

        btnMasukTab = findViewById(R.id.btnMasukTab);
        btnDaftarTab = findViewById(R.id.btnDaftarTab);

        // Login UI
        inputPhoneLogin = findViewById(R.id.inputPhoneLogin);
        inputPasswordLogin = findViewById(R.id.inputPasswordLogin);
        btnTogglePassLogin = findViewById(R.id.btnTogglePassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        textForgot = findViewById(R.id.textForgot);

        // Register UI
        inputNamaReg = findViewById(R.id.inputNamaReg);
        inputPhoneReg = findViewById(R.id.inputPhoneReg);
        inputRtReg = findViewById(R.id.inputRtReg);
        inputPassReg = findViewById(R.id.inputPassReg);
        btnTogglePassReg = findViewById(R.id.btnTogglePassReg);
        btnRegisterAction = findViewById(R.id.btnRegisterAction);
    }

    private void setupTabs() {
        // Klik Tab MASUK
        btnMasukTab.setOnClickListener(v -> {
            if (layoutLogin.getVisibility() == View.VISIBLE) return;

            // Animasi Magic Android
            TransitionManager.beginDelayedTransition(mainContainer);

            layoutRegister.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);

            // Ubah Style Tombol Tab
            updateTabStyle(true);
        });

        // Klik Tab DAFTAR
        btnDaftarTab.setOnClickListener(v -> {
            if (layoutRegister.getVisibility() == View.VISIBLE) return;

            // Animasi Magic Android
            TransitionManager.beginDelayedTransition(mainContainer);

            layoutLogin.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.VISIBLE);

            // Ubah Style Tombol Tab
            updateTabStyle(false);
        });
    }

    private void updateTabStyle(boolean isLoginActive) {
        if (isLoginActive) {
            // Masuk Aktif: Putih, Teks Hitam
            btnMasukTab.setBackgroundResource(R.drawable.rounded_button);
            btnMasukTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnMasukTab.setTextColor(Color.BLACK);

            // Daftar Inaktif: Transparan, Teks Abu
            btnDaftarTab.setBackgroundColor(Color.TRANSPARENT);
            btnDaftarTab.setTextColor(ContextCompat.getColor(this, R.color.gray));
        } else {
            // Daftar Aktif: Putih, Teks Hitam
            btnDaftarTab.setBackgroundResource(R.drawable.rounded_button);
            btnDaftarTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnDaftarTab.setTextColor(Color.BLACK);

            // Masuk Inaktif: Transparan, Teks Abu
            btnMasukTab.setBackgroundColor(Color.TRANSPARENT);
            btnMasukTab.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }
    }

    private void setupPasswordToggles() {
        // Toggle Login Password
        btnTogglePassLogin.setOnClickListener(v -> {
            isLoginPassVisible = togglePassword(inputPasswordLogin, btnTogglePassLogin, isLoginPassVisible);
        });

        // Toggle Register Password
        btnTogglePassReg.setOnClickListener(v -> {
            isRegPassVisible = togglePassword(inputPassReg, btnTogglePassReg, isRegPassVisible);
        });
    }

    private boolean togglePassword(EditText input, ImageView icon, boolean isVisible) {
        if (isVisible) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.icon_mata2); // Mata tertutup
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.icon_mata1); // Mata terbuka
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

        btnRegisterAction.setOnClickListener(v -> {
            performRegister();
        });
        textForgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }

    // --- LOGIC API (Diambil dari kode lama Anda) ---

    private void performLogin(String telepon, String password) {
        String url = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/login.php"; // Cek URL Anda

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API_LOGIN", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONObject user = obj.getJSONObject("data");
                            String nama = user.getString("nama");
                            String id_warga = String.valueOf(user.getInt("id"));
                            String id_rt = user.getString("id_rt");

                            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("id", id_warga)
                                    .putString("id_rt", id_rt)
                                    .putString("nama", nama)  // <--- TAMBAHKAN BARIS INI
                                    .apply();

                            Toast.makeText(this, "Selamat datang, " + nama, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("nama_user", nama);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Login Gagal: " + error.getMessage(), Toast.LENGTH_SHORT).show()
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

    private void performRegister() {
        String nama = inputNamaReg.getText().toString();
        String telepon = inputPhoneReg.getText().toString();
        String id_rt = inputRtReg.getText().toString();
        String password = inputPassReg.getText().toString();

        if(nama.isEmpty() || telepon.isEmpty() || id_rt.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://oldest-widely-shell-produced.trycloudflare.com/jagawarga/register.php"; // Cek URL Anda

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API_REG", response);
                    Toast.makeText(this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show();

                    // Otomatis pindah ke tab login setelah sukses
                    btnMasukTab.performClick();
                },
                error -> Toast.makeText(this, "Gagal Daftar: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("telepon", telepon);
                params.put("id_rt", id_rt);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}