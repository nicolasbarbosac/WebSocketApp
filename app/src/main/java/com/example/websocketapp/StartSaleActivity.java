package com.example.websocketapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cl.tbk.apos.sdk.model.ErrorResponse;
import cl.tbk.apos.sdk.model.VentaResponse;
import cl.transbank.pagoappsdk.SdkActivityLauncher;
import cl.transbank.pagoappsdk.domain.CurrencyType;
import cl.transbank.pagoappsdk.domain.wrapper.BundleWraper;
import cl.transbank.pagoappsdk.domain.wrapper.RequestStatus;

public class StartSaleActivity extends AppCompatActivity {
    SdkActivityLauncher pago = new SdkActivityLauncher();
    String apiKey = "RBZD25SKEATW0J070X6C";
    String flavor = "dev";
    TextView TxtInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_integration);
        TxtInfo= findViewById(R.id.txtInfo);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String amount = extras.getString("AMOUNT");

                Log.d("StartSaleActivity --**--",amount);
                RequestStatus status = pago.requestForSales(this, apiKey, amount, CurrencyType.PESO, flavor);
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        processSalesResponse(requestCode, resultCode, data);
    }

    private void processSalesResponse(int requestCode, int resultCode, @Nullable Intent data) {

        BundleWraper salesWrapper = pago.getSalesResponseFromActivityResult(requestCode, resultCode, data);
        ErrorResponse errorResponse;
        VentaResponse ventaResponse;
        switch (salesWrapper.getStatus()) {
            case REQUEST_OK:
                ventaResponse = (VentaResponse) salesWrapper.getResponse();
                goToShowInJsonFormat(ventaResponse, this);
                break;
            case REQUEST_CANCELLED_WITHOUT_ERROR:
            case REQUEST_CANCELLED_WITH_ERROR:
            case REQUEST_ERROR:
                errorResponse = (ErrorResponse) salesWrapper.getResponse();
                goToShowInJsonFormatForError(errorResponse);
                break;
            default:
                Log.d("PROCESSSALESRESPONSE", "DEFAULT");
                break;
        }
    }

    private void goToShowInJsonFormat(VentaResponse ventaResponse, Context context) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(ventaResponse);
        TxtInfo.setText(json);
      //  finish();
        moveTaskToBack(true);
    }

    private void goToShowInJsonFormatForError(ErrorResponse errorResponse) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(errorResponse);
        TxtInfo.setText(json);
    //    finish();
        moveTaskToBack(true);
    }
}
