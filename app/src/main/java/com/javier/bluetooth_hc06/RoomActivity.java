package com.javier.bluetooth_hc06;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.bluetooth_hc06.util.Room;
import com.javier.bluetooth_hc06.util.RoomSingleton;

import java.io.IOException;

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
                btSocket.getOutputStream().write((room + number).getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
        //reload();
    }

    public void reload() {
        Room model = RoomSingleton.getInstance().getRoom(room);
        textView.setText(model.getTemperature()+"ºC");
        textView2.setText(model.getHumidity()+"%");
        imageView.setImageResource(R.drawable.ic_lightbulb_outline_black_48dp);
        imageView2.setImageResource(R.drawable.ic_person_black_48dp);
        imageView3.setImageResource(R.drawable.ic_volume_up_black_48dp);
        imageView4.setImageResource(R.drawable.ic_alarm_black_48dp);
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
