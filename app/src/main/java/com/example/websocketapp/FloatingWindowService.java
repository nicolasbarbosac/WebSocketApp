package com.example.websocketapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import cl.transbank.pagoappsdk.SdkActivityLauncher;
import cl.transbank.pagoappsdk.domain.CurrencyType;
import cl.transbank.pagoappsdk.domain.wrapper.RequestStatus;

public class FloatingWindowService extends LifecycleService {

    private WindowManager windowManager;
    private View floatingView;
String amount="";
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
         super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                amount= extras.getString("AMOUNT");
                Log.d("FloatingWindowService--**--",amount);


                Intent intentSale = new Intent(this, StartSaleActivity.class);
                intentSale.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Log.d("AMOUNT --**--",amount);
                intentSale.putExtra("AMOUNT", amount);
                startActivity(intentSale);

                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Verificar si la app tiene permiso para dibujar sobre otras aplicaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // Si no tiene permiso, abortar el servicio
            return;
        }

        // Inicializar el WindowManager
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // Inflar el layout de la ventana flotante
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        floatingView = inflater.inflate(R.layout.floating_window_layout, null);

        // Configuración de parámetros de la ventana flotante
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // Ubicación de la ventana flotante (top-left)
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = 0;
        layoutParams.y = 100;

        // Añadir la vista flotante al WindowManager
        windowManager.addView(floatingView, layoutParams);

        // Configurar el botón de cierre en la ventana flotante
        Button closeButton = floatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> stopSelf());  // Detener el servicio al cerrar

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) {
            windowManager.removeView(floatingView);  // Eliminar la vista flotante
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }


}