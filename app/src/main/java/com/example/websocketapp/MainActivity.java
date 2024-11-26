package com.example.websocketapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import cl.transbank.pagoappsdk.SdkActivityLauncher;
import cl.transbank.pagoappsdk.domain.CurrencyType;
import cl.transbank.pagoappsdk.domain.wrapper.RequestStatus;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_PERMISSION = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnPago = findViewById(R.id.BtnPago);
        AppCompatActivity act = this;

        if (!Settings.canDrawOverlays(MainActivity.this)) {
            // Si no tiene el permiso, solicitarlo
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, REQUEST_CODE_PERMISSION);
        }

        btnPago.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                // Si no tiene el permiso, solicitarlo
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(intent, REQUEST_CODE_PERMISSION);
            }
//            // Iniciar el servicio flotante como servicio en primer plano
//            Intent intent = new Intent(MainActivity.this, FloatingWindowService.class);
//            startService(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Log.d("tag", "on create");
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        startService(serviceIntent);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                startFloatingService();
            } else {
                Toast.makeText(this, "Permiso no concedido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startFloatingService() {
        Intent intent = new Intent(MainActivity.this, FloatingWindowService.class);
        startService(intent);
    }

}