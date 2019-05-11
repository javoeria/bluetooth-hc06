package com.javier.bluetooth_hc06.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.cloud.AuthCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BigQueryTask extends AsyncTask<String, Integer, String> {
    private Context context;

    public BigQueryTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String JSON_CONTENT = RoomSingleton.getInstance().getJSON("A") + "\r\n"
                + RoomSingleton.getInstance().getJSON("B") + "\r\n"
                + RoomSingleton.getInstance().getJSON("C");
        int num = 0;
        try {
            InputStream isCredentialsFile = context.getAssets().open("Data-bb1b288f9cfa.json");
            BigQuery bigquery = BigQueryOptions.builder()
                    .authCredentials(AuthCredentials.createForJson(isCredentialsFile))
                    .projectId("data-234914")
                    .build().service();

            TableId tableId = TableId.of("android_app", "test");
            WriteChannelConfiguration configuration = WriteChannelConfiguration.builder(tableId)
                    .formatOptions(FormatOptions.json())
                    .build();
            WriteChannel channel = bigquery.writer(configuration);

            Log.d("Main", "Sending JSON: " + JSON_CONTENT);
            num = channel.write(ByteBuffer.wrap(JSON_CONTENT.getBytes(StandardCharsets.UTF_8)));
            Log.d("Main", "Loading " + num + " bytes into table " + tableId);
            channel.close();
        } catch (IOException e) {
            Log.d("Main", "Exception: " + e.toString());
        }
        return "Done";
    }

    @Override
    protected void onPostExecute(String msg) {
        super.onPostExecute(msg);
    }
}