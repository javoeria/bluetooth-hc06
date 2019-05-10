package com.javier.bluetooth_hc06;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.cloud.AuthCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.javier.bluetooth_hc06.util.Room;
import com.javier.bluetooth_hc06.util.RoomSingleton;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

        CardView btn1 = findViewById(R.id.cardView);
        CardView btn2 = findViewById(R.id.cardView2);
        CardView btn3 = findViewById(R.id.cardView3);
        CardView btn4 = findViewById(R.id.cardView4);
        CardView btn5 = findViewById(R.id.cardView5);
        CardView btn6 = findViewById(R.id.cardView6);

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

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("5");
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("6");
            }
        });
    }

    private void sendSignal(String number) {
        Room model = RoomSingleton.getInstance().getRoom(room);
        model.setTemperature(model.getTemperature()+1);
        model.setHumidity(model.getHumidity()+1);
        RoomSingleton.getInstance().setRoom(room, model);
        reload();

        msg(room + "_" + number);
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write((room + number).getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void reload() {
        Room model = RoomSingleton.getInstance().getRoom(room);

        textView.setText(String.valueOf(model.getTemperature())+"ºC");
        textView2.setText(String.valueOf(model.getHumidity())+"%");
        imageView.setImageResource(R.drawable.ic_lightbulb_outline_black_48dp);
        imageView2.setImageResource(R.drawable.ic_person_black_48dp);
        imageView3.setImageResource(R.drawable.ic_volume_up_black_48dp);
        imageView4.setImageResource(R.drawable.ic_alarm_black_48dp);
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
                finish();
                return true;
            case R.id.refresh_menu:
                new BigQueryTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class BigQueryTask extends AsyncTask<String, Integer, String> {
        private final String CREDENTIALS_FILE = "Data-bb1b288f9cfa.json";
        private final String PROJECT_ID = "data-234914";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Sending JSON...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String JSON_CONTENT_TEST = "{\"X\": \"1.0\", \"Y\": \"2.0\", \"Z\": \"3.0\"}";
            String JSON_CONTENT_ARRAY_TEST = "{\"X\": \"1.0\", \"Y\": \"2.0\", \"Z\": \"3.0\"}\r\n{\"X\": \"4.0\", \"Y\": \"5.0\", \"Z\": \"6.0\"}";

            String JSON_CONTENT = "{\"X\": \"1.0\", \"Y\": \"2.0\", \"Z\": \"3.0\", \"date\": \"" + new DateTime(new Date()) + "\"}";
            //String JSON_CONTENT = params[0];
            int num = 0;
            try {
                InputStream isCredentialsFile = getAssets().open(CREDENTIALS_FILE);
                BigQuery bigquery = BigQueryOptions.builder()
                        .authCredentials(AuthCredentials.createForJson(isCredentialsFile))
                        .projectId(PROJECT_ID)
                        .build().service();

                TableId tableId = TableId.of("android_app", "test");
                // Table table = bigquery.getTable(tableId);

                WriteChannelConfiguration configuration = WriteChannelConfiguration.builder(tableId)
                        .formatOptions(FormatOptions.json())
                        .build();
                WriteChannel channel = bigquery.writer(configuration);

                Log.d("Main", "Sending JSON: " + JSON_CONTENT);
                num = channel.write(ByteBuffer.wrap(JSON_CONTENT.getBytes(StandardCharsets.UTF_8)));
                Log.d("Main", "Loading " + Integer.toString(num) + " bytes into table " + tableId);
                channel.close();
            } catch (Exception e) {
                Log.d("Main", "Exception: " + e.toString());
            }
            return "Done";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String msg) {
            super.onPostExecute(msg);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
