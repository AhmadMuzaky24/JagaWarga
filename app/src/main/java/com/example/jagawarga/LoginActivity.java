package com.example.jagawarga;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagawarga.databinding.ActivityLoginBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean[] isPasswordVisible = {false};

        binding.btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                // Hide password
                binding.inputPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                binding.btnTogglePassword.setImageResource(R.drawable.icon_mata2);
            } else {
                // Show password
                binding.inputPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                binding.btnTogglePassword.setImageResource(R.drawable.icon_mata1);
            }

            // Agar cursor tetap di akhir teks
            binding.inputPassword.setSelection(binding.inputPassword.getText().length());

            isPasswordVisible[0] = !isPasswordVisible[0];
        });


        // Tombol "Masuk"
        binding.btnLogin.setOnClickListener(v -> {
            String phone = binding.inputPhone.getText().toString().trim();
            String pass = binding.inputPassword.getText().toString().trim();

            if (phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show();
            } else {
                login();
            }
        });

        binding.btnMasukTab2.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // opsional: supaya user tidak kembali ke login setelah tekan back
        });

    }
    private void login() {
        String telepon = binding.inputPhone.getText().toString();
        String password = binding.inputPassword.getText().toString();

        String url = "https://writing-aimed-afterwards-prefers.trycloudflare.com/jagawarga/login.php";

        Log.d("DEBUG_LOGIN", "Mengirim request ke: " + url);



        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API_LOGIN", response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("success")) {
                            JSONObject user = obj.getJSONObject("data");

                            String nama = user.getString("nama");

                            Toast.makeText(this, "Selamat datang, " + nama, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("nama_user", nama);
                            startActivity(intent);
                            finish(); // opsional: supaya user tidak kembali ke login setelah tekan back

                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },

                error -> {
                    Log.e("API_LOGIN_ERROR", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("API_LOGIN_ERROR", "Status: " + error.networkResponse.statusCode);
                        Log.e("API_LOGIN_BODY", new String(error.networkResponse.data));
                    }
                    Toast.makeText(this, "Login gagal: " + error.toString(), Toast.LENGTH_LONG).show();
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("telepon", telepon);
                params.put("password", password);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


}

