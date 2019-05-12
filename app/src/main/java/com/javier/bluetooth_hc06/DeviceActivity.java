package com.javier.bluetooth_hc06;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.weathericonview.WeatherIconView;
import com.javier.bluetooth_hc06.util.MyHandler;
import com.javier.bluetooth_hc06.util.Room;
import com.javier.bluetooth_hc06.util.RoomSingleton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DeviceActivity extends AppCompatActivity {

    private String address = "";
    protected static BluetoothSocket btSocket = null;
    protected static MyHandler mHandlerThread;
    public static final String EXTRA_ROOM = "device_room";
    private TextView textA1, textA2, textB1, textB2, textC1, textC2;
    private ImageView imageA1, imageA2, imageA3, imageA4, imageA5, imageA6;
    private ImageView imageB1, imageB2, imageB3, imageB4, imageB5, imageB6;
    private ImageView imageC1, imageC2, imageC3, imageC4, imageC5, imageC6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        address = getIntent().getStringExtra(MainActivity.EXTRA_ADDRESS);
        setWeatherIcon();
        new ConnectBT().execute();
        mHandlerThread = new MyHandler(this);

        ImageButton btnA = findViewById(R.id.imageButton);
        ImageButton btnB = findViewById(R.id.imageButton2);
        ImageButton btnC = findViewById(R.id.imageButton3);

        textA1 = findViewById(R.id.textViewA1);
        textA2 = findViewById(R.id.textViewA2);
        imageA1 = findViewById(R.id.imageViewA1);
        imageA2 = findViewById(R.id.imageViewA2);
        imageA3 = findViewById(R.id.imageViewA3);
        imageA4 = findViewById(R.id.imageViewA4);
        imageA5 = findViewById(R.id.imageViewA5);
        imageA6 = findViewById(R.id.imageViewA6);

        textB1 = findViewById(R.id.textViewB1);
        textB2 = findViewById(R.id.textViewB2);
        imageB1 = findViewById(R.id.imageViewB1);
        imageB2 = findViewById(R.id.imageViewB2);
        imageB3 = findViewById(R.id.imageViewB3);
        imageB4 = findViewById(R.id.imageViewB4);
        imageB5 = findViewById(R.id.imageViewB5);
        imageB6 = findViewById(R.id.imageViewB6);

        textC1 = findViewById(R.id.textViewC1);
        textC2 = findViewById(R.id.textViewC2);
        imageC1 = findViewById(R.id.imageViewC1);
        imageC2 = findViewById(R.id.imageViewC2);
        imageC3 = findViewById(R.id.imageViewC3);
        imageC4 = findViewById(R.id.imageViewC4);
        imageC5 = findViewById(R.id.imageViewC5);
        imageC6 = findViewById(R.id.imageViewC6);

        reload("A");
        reload("B");
        reload("C");

        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                enterRoom("A");
            }
        });

        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                enterRoom("B");
            }
        });

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                enterRoom("C");
            }
        });
    }

    private void enterRoom(String room) {
        Intent i = new Intent(DeviceActivity.this, RoomActivity.class);
        i.putExtra(EXTRA_ROOM, room);
        startActivity(i);
    }

    private void disconnect() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if ( btSocket != null ) {
                            try {
                                btSocket.close();
                            } catch(IOException e) {
                                Log.d("Main", "IOException: " + e.getMessage());
                                msg("Error");
                            }
                        }
                        msg("Disconnected");
                        finish();
                    }
                }).show();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void setWeatherIcon() {
        WeatherIconView weatherIconView = findViewById(R.id.my_weather_icon);
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH");
        int hour = Integer.valueOf(format.format(d));
        if (hour > 7 && hour <= 11) {
            weatherIconView.setIconResource(getString(R.string.wi_day_sunny_overcast));
        } else if (hour > 11 && hour <= 18) {
            weatherIconView.setIconResource(getString(R.string.wi_day_sunny));
        } else if (hour > 18 && hour <= 22) {
            weatherIconView.setIconResource(getString(R.string.wi_day_cloudy));
        } else {
            weatherIconView.setIconResource(getString(R.string.wi_night_clear));
        }
    }

    public void reload(String room) {
        if (room.equals("A")) {
            reloadA();
        } else if (room.equals("B")) {
            reloadB();
        } else {
            reloadC();
        }
    }

    private void reloadA() {
        Room modelA = RoomSingleton.getInstance().getRoom("A");
        textA1.setText(modelA.getTemperature() + "ºC");
        textA2.setText(modelA.getHumidity() + "%");
        if (modelA.isLight()) {
            imageA3.setImageResource(R.drawable.light_on_96);
        } else {
            imageA3.setImageResource(R.drawable.light_off_96);
        }
        if (modelA.isPresence()) {
            imageA4.setImageResource(R.drawable.presence_on_96);
        } else {
            imageA4.setImageResource(R.drawable.presence_off_96);
        }
        if (modelA.isMusic()) {
            imageA5.setImageResource(R.drawable.music_on_96);
        } else {
            imageA5.setImageResource(R.drawable.music_off_96);
        }
        if (modelA.isAlarm()) {
            imageA6.setImageResource(R.drawable.alarm_on_96);
        } else {
            imageA6.setImageResource(R.drawable.alarm_off_96);
        }
    }

    private void reloadB() {
        Room modelB = RoomSingleton.getInstance().getRoom("B");
        textB1.setText(modelB.getTemperature() + "ºC");
        textB2.setText(modelB.getHumidity() + "%");
        if (modelB.isLight()) {
            imageB3.setImageResource(R.drawable.light_on_96);
        } else {
            imageB3.setImageResource(R.drawable.light_off_96);
        }
        if (modelB.isPresence()) {
            imageB4.setImageResource(R.drawable.presence_on_96);
        } else {
            imageB4.setImageResource(R.drawable.presence_off_96);
        }
        if (modelB.isMusic()) {
            imageB5.setImageResource(R.drawable.music_on_96);
        } else {
            imageB5.setImageResource(R.drawable.music_off_96);
        }
        if (modelB.isAlarm()) {
            imageB6.setImageResource(R.drawable.alarm_on_96);
        } else {
            imageB6.setImageResource(R.drawable.alarm_off_96);
        }
    }

    private void reloadC() {
        Room modelC = RoomSingleton.getInstance().getRoom("C");
        textC1.setText(modelC.getTemperature() + "ºC");
        textC2.setText(modelC.getHumidity() + "%");
        if (modelC.isLight()) {
            imageC3.setImageResource(R.drawable.light_on_96);
        } else {
            imageC3.setImageResource(R.drawable.light_off_96);
        }
        if (modelC.isPresence()) {
            imageC4.setImageResource(R.drawable.presence_on_96);
        } else {
            imageC4.setImageResource(R.drawable.presence_off_96);
        }
        if (modelC.isMusic()) {
            imageC5.setImageResource(R.drawable.music_on_96);
        } else {
            imageC5.setImageResource(R.drawable.music_off_96);
        }
        if (modelC.isAlarm()) {
            imageC6.setImageResource(R.drawable.alarm_on_96);
        } else {
            imageC6.setImageResource(R.drawable.alarm_off_96);
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private boolean connected = false;
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(DeviceActivity.this, "Connecting...", "Please Wait");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if ( btSocket==null || !connected ) {
                    BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = myBluetooth.getRemoteDevice(address);
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                    myBluetooth.cancelDiscovery();
                    btSocket.connect();
                    btSocket.getOutputStream().write("00".getBytes());
                    connected = true;
                }
            } catch (IOException e) {
                Log.d("Main", "IOException: " + e.getMessage());
                connected = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!connected) {
                msg("Connection Failed. Try again");
                finish();
            } else {
                msg("Connected");
                receiveData();
            }
            progress.dismiss();
        }

        private void receiveData() {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    Log.d("Main", "Run thread");
                    byte[] buffer = new byte[1024];
                    int bytes;

                    while (btSocket.isConnected()) {
                        try {
                            String readMessage = "";
                            while(!readMessage.contains("}")) {
                                bytes = btSocket.getInputStream().read(buffer);
                                readMessage += new String(buffer, 0, bytes);
                            }
                            mHandlerThread.sendMessage(Message.obtain(mHandlerThread, MyHandler.UPDATE_ALL, readMessage));
                        } catch (IOException e) {
                            Log.d("Main", "IOException: " + e.getMessage());
                            break;
                        }
                    }
                }
            });
            thread.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close_menu:
                disconnect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        disconnect();
    }
}
