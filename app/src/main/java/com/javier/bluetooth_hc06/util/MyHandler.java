package com.javier.bluetooth_hc06.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.javier.bluetooth_hc06.DeviceActivity;
import com.javier.bluetooth_hc06.RoomActivity;

import org.json.JSONObject;

public class MyHandler extends Handler {

    public static final int UPDATE_ALL = 0;
    public static final int UPDATE_A = 1;
    public static final int UPDATE_B = 2;
    public static final int UPDATE_C = 3;
    private DeviceActivity parent;
    private RoomActivity room;
    private int count = 0;

    public MyHandler(DeviceActivity parent) {
        super();
        this.parent = parent;
        this.room = null;
    }

    public void setRoom(RoomActivity room) {
        this.room = room;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case UPDATE_ALL:
                try {
                    String m = (String) msg.obj;
                    JSONObject json = new JSONObject(m);
                    Log.d("Main", json.toString());

                    String letter = json.getString("room");
                    int temperature = json.getInt("temperature");
                    int humidity = json.getInt("humidity");
                    boolean light = json.getInt("light") == 1;
                    boolean presence = json.getInt("presence") == 1;
                    boolean music = json.getInt("music") == 1;
                    boolean alarm = json.getInt("alarm") == 1;

                    Room model = RoomSingleton.getInstance().getRoom(letter);
                    model.setTemperature(temperature);
                    model.setHumidity(humidity);
                    model.setLight(light);
                    model.setPresence(presence);
                    model.setMusic(music);
                    model.setAlarm(alarm);

                    RoomSingleton.getInstance().setRoom(letter, model);
                    if (room != null) {
                        room.reload();
                    }
                    parent.reload(letter);
                    count++;
                    if (count >= 6) {
                        new BigQueryTask(parent.getApplicationContext()).execute();
                        count = 0;
                        msg("Updated");
                    }
                } catch (Throwable t) {
                    Log.d("Main", "Throwable: " + t.getMessage());
                    msg("Error");
                }
                break;
            case UPDATE_A:
                msg("update_room_a");
                break;
            case UPDATE_B:
                msg("update_room_b");
                break;
            case UPDATE_C:
                msg("update_room_c");
                break;
            default:
                super.handleMessage(msg);
        }
    }

    private void msg(String s) {
        Toast.makeText(parent, s, Toast.LENGTH_SHORT).show();
    }
}
