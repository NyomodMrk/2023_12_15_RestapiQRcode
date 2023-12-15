package com.example.a2023_12_15_restapiqrcode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    private Button btn_main_scan;
    private Button btn_main_listazas;
    private TextView txtview_main;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btn_main_listazas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListAdatok.class);
                startActivity(intent);
                finish();
            }
        });

        btn_main_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setPrompt("Qr Code olvasó by petrik");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.initiateScan();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, "kiléptél a QRCode olvasóból",
                        Toast.LENGTH_SHORT).show();
            } else {
                txtview_main.setText(result.getContents());
                Uri uri = Uri.parse(result.getContents());
                if (URLUtil.isValidUrl(result.getContents())) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        } else {
            super.onActivityResult(resultCode, resultCode, data);
        }
    }

    protected void init() {
        btn_main_listazas = findViewById(R.id.btn_main_listazas);
        btn_main_scan = findViewById(R.id.btn_main_scan);
        txtview_main = findViewById(R.id.txtview_main);

        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String seged = sharedPreferences.getString("adat", "Nincs ilyen adat");
        txtview_main.setText(seged);
    }
}