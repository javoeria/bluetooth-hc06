package com.javier.bluetooth_hc06.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.javier.bluetooth_hc06.DeviceActivity;
import com.javier.bluetooth_hc06.R;

import static com.javier.bluetooth_hc06.MainActivity.EXTRA_ADDRESS;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private String address;

    public FingerprintHandler(Context mContext, String mAddress) {
        context = mContext;
        address = mAddress;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Fingerprint Authentication Error\n" + errString, false);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Fingerprint Authentication Help\n" + helpString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Failed to authenticate fingerprint", false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Success in authenticating fingerprint", true);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);

        Intent i = new Intent(context, DeviceActivity.class);
        i.putExtra(EXTRA_ADDRESS, address);
        context.startActivity(i);
        //((Activity)context).recreate();
    }

    private void update(String e, Boolean success){
        TextView textView = ((Activity)context).findViewById(R.id.errorText);
        textView.setText(e);
        if (success) {
            textView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
        }
    }
}