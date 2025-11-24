package com.example.jagawarga;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagawarga.databinding.ActivityRegisterBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> register());

        // tombol kembali ke login
        binding.btnMasukTab.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        boolean[] isPasswordVisible = {false};
        binding.btnTogglePassword2.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                binding.inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.btnTogglePassword2.setImageResource(R.drawable.icon_mata2);
            } else {
                binding.inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.btnTogglePassword2.setImageResource(R.drawable.icon_mata1);
            }
            binding.inputPassword.setSelection(binding.inputPassword.getText().length());
            isPasswordVisible[0] = !isPasswordVisible[0];
        });

        EditText inputPhone2 = findViewById(R.id.inputPhone2);
        inputPhone2.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(2) // 2 digit desimal
        });

    }

    private void register() {
        String nama = binding.inputNama.getText().toString();
        String telepon = binding.inputPhone4.getText().toString();
        String id_rt = binding.inputPhone2.getText().toString();
        String password = binding.inputPassword.getText().toString();

        String url = "https://intl-edited-sticker-jam.trycloudflare.com/jagawarga/register.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API_RESPONSE", response);   // <--- lihat JSON realtime di Logcat
                    Toast.makeText(this, "Berhasil Daftar!\nSilahkan Tunggu, Datamu Lagi Diverifikasi", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("API_ERROR", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("API_ERROR", "Status: " + error.networkResponse.statusCode);
                        Log.e("API_ERROR", "Body: " + new String(error.networkResponse.data));
                    }
                }

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


