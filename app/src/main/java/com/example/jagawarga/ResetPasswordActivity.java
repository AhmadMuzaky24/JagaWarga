package com.example.jagawarga;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputPhone, inputPass;
    private Button btnReset;
    private ImageButton btnBack;
    private ImageView btnTogglePass;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();
        setupListeners();
    }

    private void initViews() {
        inputPhone = findViewById(R.id.inputPhoneWarga);
        inputPass = findViewById(R.id.inputPass);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);
        btnTogglePass = findViewById(R.id.btnTogglePass);
    }

    private void setupListeners() {
        // Tombol Kembali
        btnBack.setOnClickListener(v -> finish());

        // Toggle Password (Lihat/Sembunyikan)
        btnTogglePass.setOnClickListener(v -> {
            if (isPasswordVisible) {
                inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePass.setImageResource(R.drawable.icon_mata2); // Pastikan icon ini ada
            } else {
                inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePass.setImageResource(R.drawable.icon_mata1); // Pastikan icon ini ada
            }
            inputPass.setSelection(inputPass.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // Tombol Reset
        btnReset.setOnClickListener(v -> {
            String phone = inputPhone.getText().toString().trim();
            String newPass = inputPass.getText().toString().trim();

            if (phone.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Isi nomor telepon dan password baru!", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(phone, newPass);
            }
        });
    }

    private void resetPassword(String phone, String newPass) {
        // Ganti URL sesuai server Anda
        String url = "https://newsletter-cod-jeff-cement.trycloudflare.com/jagawarga/change_password.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(this, "Password berhasil diubah! Silakan login.", Toast.LENGTH_LONG).show();
                            finish(); // Kembali ke login
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal menghubungi server: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("telepon", phone);
                params.put("new_password", newPass);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}