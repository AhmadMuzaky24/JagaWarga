package com.example.jagawarga;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
public class PrefUtils {
    public static String getIdRt(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String idRt = prefs.getString("id_rt", null);

        if (idRt == null) {
            Toast.makeText(context, "ID RT tidak ditemukan, silakan login ulang!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("DEBUG_RT", "ID RT dari SharedPreferences = " + idRt);
        }

        return idRt;
    }

    public static String getIdWarga(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String idWarga = prefs.getString("id_warga", null);

        if (idWarga == null) {
            Toast.makeText(context, "ID Warga tidak ditemukan, silakan login ulang!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("DEBUG_WARGA", "ID Warga dari SharedPreferences = " + idWarga);
        }

        return idWarga;
    }
}
