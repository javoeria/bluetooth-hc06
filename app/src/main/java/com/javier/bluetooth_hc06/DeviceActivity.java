package com.javier.bluetooth_hc06;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.pwittchen.weathericonview.WeatherIconView;
import com.javier.bluetooth_hc06.util.MyAdapter;
import com.javier.bluetooth_hc06.util.MyHandler;
import com.javier.bluetooth_hc06.util.Room;
import com.javier.bluetooth_hc06.util.RoomSingleton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DeviceActivity extends AppCompatActivity {

    private String address = "";
    private MyAdapter adapter;
    protected static BluetoothSocket btSocket = null;
    protected static MyHandler mHandlerThread;
    public static final String EXTRA_ROOM = "device_room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        address = getIntent().getStringExtra(MainActivity.EXTRA_ADDRESS);
        setWeatherIcon();
        new ConnectBT().execute();
        mHandlerThread = new MyHandler(this);

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(RoomSingleton.getInstance().getRoom("A"));
        rooms.add(RoomSingleton.getInstance().getRoom("B"));
        rooms.add(RoomSingleton.getInstance().getRoom("C"));
        adapter = new MyAdapter(rooms);
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
            adapter.notifyItemChanged(0);
        } else if (room.equals("B")) {
            adapter.notifyItemChanged(1);
        } else {
            adapter.notifyItemChanged(2);
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
