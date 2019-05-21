package com.javier.bluetooth_hc06;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.bluetooth_hc06.util.Room;
import com.javier.bluetooth_hc06.util.RoomSingleton;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class RoomActivity extends AppCompatActivity {

    private String room = "";
    private BluetoothSocket btSocket = null;
    private TextView textView, textView2;
    private ImageView imageView, imageView2, imageView3, imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        btSocket = DeviceActivity.btSocket;
        room = getIntent().getStringExtra(DeviceActivity.EXTRA_ROOM);
        getSupportActionBar().setTitle("ROOM " + room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DeviceActivity.mHandlerThread.setRoom(this);

        CardView btn1 = findViewById(R.id.cardView3);
        CardView btn2 = findViewById(R.id.cardView4);
        CardView btn3 = findViewById(R.id.cardView5);
        CardView btn4 = findViewById(R.id.cardView6);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);

        reload();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("1");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("2");
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("3");
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("4");
            }
        });
    }

    private void sendSignal(String number) {
        msg(room + "_" + number);
        if ( btSocket != null ) {
            try {
                String message = hmac(room + number);
                btSocket.getOutputStream().write(message.getBytes());
            } catch (IOException e) {
                Log.d("Main", "IOException: " + e.getMessage());
                msg("Error");
            }
        }
        //reload();
    }

    public void reload() {
        Room model = RoomSingleton.getInstance().getRoom(room);
        textView.setText(model.getTemperature() + "ºC");
        textView2.setText(model.getHumidity() + "%");
        if (model.isLight()) {
            imageView.setImageResource(R.drawable.light_on_96);
        } else {
            imageView.setImageResource(R.drawable.light_off_96);
        }
        if (model.isPresence()) {
            imageView2.setImageResource(R.drawable.presence_on_96);
        } else {
            imageView2.setImageResource(R.drawable.presence_off_96);
        }
        if (model.isMusic()) {
            imageView3.setImageResource(R.drawable.music_on_96);
        } else {
            imageView3.setImageResource(R.drawable.music_off_96);
        }
        if (model.isAlarm()) {
            imageView4.setImageResource(R.drawable.alarm_on_96);
        } else {
            imageView4.setImageResource(R.drawable.alarm_off_96);
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DeviceActivity.mHandlerThread.setRoom(null);
                finish();
                return true;
            case R.id.refresh_menu:
                try {
                    btSocket.getOutputStream().write(hmac("00").getBytes());
                    msg("Refresh");
                } catch (IOException e) {
                    Log.d("Main", "IOException: " + e.getMessage());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String hmac(String str) {
        String base64 = "";
        try {
            String key = BuildConfig.secret;
            Mac hasher = Mac.getInstance("HmacSHA256");
            hasher.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));

            byte[] hash = hasher.doFinal(str.getBytes());
            base64 = Base64.encodeToString(hash, android.util.Base64.DEFAULT);
            Log.d("Main", base64);
        } catch (NoSuchAlgorithmException e) {
            Log.d("Main", "Exception: " + e.getMessage());
        } catch (InvalidKeyException e) {
            Log.d("Main", "Exception: " + e.getMessage());
        }
        return base64.trim();
    }
}
