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

    private ViewGroup mainContainer;
    private LinearLayout layoutLogin, layoutRegister;
    private Button btnMasukTab, btnDaftarTab;
    private EditText inputPhoneLogin, inputPasswordLogin;
    private ImageView btnTogglePassLogin;
    private Button btnLogin;
    private TextView textForgot;
    private EditText inputNamaReg, inputPhoneReg, inputPassReg;
    private Spinner inputRtReg;
    private ImageView btnTogglePassReg;
    private Button btnRegisterAction;

    private boolean isLoginPassVisible = false;
    private boolean isRegPassVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- MATIKAN INI SAAT TESTING AGAR TIDAK LANGSUNG MASUK DASHBOARD ---
        // checkSession();

        initViews();
        setupSpinnerRt();
        setupTabs();
        setupPasswordToggles();
        setupActionButtons();
    }

    // Cek apakah user masih login (Auto Login)
    private void checkSession() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String savedId = prefs.getString("id", null);
        String savedRole = prefs.getString("role", null);
        String savedNama = prefs.getString("nama", "User");

        if (savedId != null && savedRole != null) {
            Log.d("SESSION", "User found: " + savedNama + " Role: " + savedRole);
            redirectDashboard(savedRole, savedNama);
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

    // --- LOGIC LOGIN UTAMA ---
    private void performLogin(String telepon, String password) {
        // UPDATE URL INI SESUAI TUNNEL TERBARU KAMU
        String url = "https://liberty-currencies-billion-release.trycloudflare.com/jagawarga/login.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API_LOGIN", "Response: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.has("success") && obj.getBoolean("success")) {
                            JSONObject user = obj.getJSONObject("data");

                            // Ambil data (Nama kolom JSON harus sama dengan di login.php)
                            String idWarga = user.optString("id", "0");
                            String nama = user.optString("nama", "Warga");
                            String idRt = user.optString("id_rt", "0");

                            // Ambil Role (Harus 'Warga', 'KetuaRT', atau 'KetuaRW')
                            String role = user.optString("role", "Warga");

                            // Simpan ke SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("id", idWarga)
                                    .putString("id_rt", idRt)
                                    .putString("nama", nama)
                                    .putString("role", role)
                                    .apply();

                            Toast.makeText(this, "Login Sukses: " + role, Toast.LENGTH_SHORT).show();

                            // Pindah halaman sesuai role
                            redirectDashboard(role, nama);

                        } else {
                            String msg = obj.optString("message", "Login Gagal");
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN_ERROR", "Parse Error: " + e.getMessage());
                        Toast.makeText(this, "Error Format Data Server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LOGIN_NETWORK", "Error: " + error.toString());
                    Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
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

    // --- LOGIC PEMBAGIAN DASHBOARD (ROLE CHECK) ---
    private void redirectDashboard(String role, String namaUser) {
        Intent intent;

        // Gunakan equalsIgnoreCase agar 'KetuaRT' sama dengan 'ketuart' (untuk jaga-jaga)
        // Tapi karena DB kamu ENUM, isinya pasti presisi 'KetuaRT' atau 'KetuaRW'

        if (role.equalsIgnoreCase("KetuaRT")) {
            intent = new Intent(LoginActivity.this, DashboardRtActivity.class);
        } else if (role.equalsIgnoreCase("KetuaRW")) {
            intent = new Intent(LoginActivity.this, DashboardRwActivity.class);
        } else {
            // Default untuk 'Warga' atau jika role kosong
            intent = new Intent(LoginActivity.this, DashboardActivity.class);
        }

        // Kirim nama user sebagai extra data
        intent.putExtra("nama_user", namaUser);

        // Hapus activity login dari stack agar user tidak bisa tekan tombol back kembali ke login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void performRegister() {
        // (Kode register sama seperti sebelumnya, tidak diubah)
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

        String url = "https://liberty-currencies-billion-release.trycloudflare.com/jagawarga/register.php";
        String finalIdRt = id_rt;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Registrasi berhasil dikirim!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, PendingRegisterActivity.class);
                    startActivity(intent);
                },
                error -> Toast.makeText(this, "Gagal Daftar", Toast.LENGTH_SHORT).show()
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