package com.example.websocketapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import cl.transbank.pagoappsdk.SdkActivityLauncher;
public class WebSocketService extends Service {


    private WebSocket webSocket;
    private Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
      //  context = MyApp.getContext();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // No necesitamos conexión a la actividad
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", "**----**");
        connectToWebSocket(); // Iniciar la conexión WebSocket
        return START_STICKY; // El servicio se reiniciará si es detenido
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Service stopped");
        }
    }


    public void connectToWebSocket() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("wss://www.vyvplatform.cl/smartpostcentralapi/ws")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d("WebSocketService", "Connection opened");
                String deviceUUID = getDeviceUUID();
                Log.d("device", "deviceUUID");
                webSocket.send("DEVICEID:" + deviceUUID);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocketService", "Message received (text): " + text);
                try{
                    if (text.startsWith("PAYMENT:")) {
                        String monto = text.split(":")[1];
                        //  RequestStatus status = pago.requestForSales(actividad, apiKey, monto, CurrencyType.PESO, flavor);
                        if (Settings.canDrawOverlays(context)) {
                            Intent intent = new Intent(context, FloatingWindowService.class);
                            intent.putExtra("AMOUNT",monto);
                            startService(intent);
                        }
                        webSocket.send("PAYMENT:1");
                        //      actividad.moveTaskToBack(true);
                    }
                }catch (Exception ex){
                    Log.d("Ex", ex.getMessage());
                }

            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.d("WebSocketService", "Message received: (byte)" + bytes.hex());
                webSocket.send("PAYMENT:1");
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.d("WebSocketService", "Closing: " + code + " / " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable okhttp3.Response response) {
                Log.e("WebSocketService", "Error: " + t.getMessage());
            }
        });

        client.dispatcher().executorService().shutdown();
    }




    private String getDeviceUUID() {
        /*return Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        */
        return "c912aae67dc1fbf9";
    }



}