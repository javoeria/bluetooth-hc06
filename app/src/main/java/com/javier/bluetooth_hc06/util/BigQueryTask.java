package com.javier.bluetooth_hc06.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
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
    private final Context context;

    public BigQueryTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String JSON_CONTENT = params[0];
        try {
            InputStream isCredentialsFile = context.getAssets().open("Data-bb1b288f9cfa.json");
            BigQuery bigquery = BigQueryOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(isCredentialsFile))
                    .setProjectId("data-234914")
                    .build().getService();

            TableId tableId = TableId.of("android_app", "test");
            WriteChannelConfiguration configuration = WriteChannelConfiguration.newBuilder(tableId)
                    .setFormatOptions(FormatOptions.json())
                    .build();
            WriteChannel channel = bigquery.writer(configuration);
            int num = channel.write(ByteBuffer.wrap(JSON_CONTENT.getBytes(StandardCharsets.UTF_8)));
            Log.d("Main", "Loading " + num + " bytes into table " + tableId.getTable());
            channel.close();
        } catch (IOException e) {
            Log.d("Main", "IOException: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String msg) {
        super.onPostExecute(msg);
    }
}