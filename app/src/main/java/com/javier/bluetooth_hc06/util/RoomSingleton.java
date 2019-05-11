package com.javier.bluetooth_hc06.util;

import android.util.Log;

import com.google.api.client.util.DateTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class RoomSingleton {

    private static final RoomSingleton ourInstance = new RoomSingleton();
    private Room modelA = new Room();
    private Room modelB = new Room();
    private Room modelC = new Room();

    private RoomSingleton() {}

    public static RoomSingleton getInstance() {
        return ourInstance;
    }

    public Room getRoom(String room) {
        if (room.equals("A")) {
            return modelA;
        } else if (room.equals("B")) {
            return modelB;
        } else {
            return modelC;
        }
    }

    public void setRoom(String room, Room model) {
        if (room.equals("A")) {
            this.modelA = model;
        } else if (room.equals("B")) {
            this.modelB = model;
        } else {
            this.modelC = model;
        }
    }

    public JSONObject getJSON(String room) {
        JSONObject json = new JSONObject();
        try {
            json.put("room", room);
            switch (room) {
                case "A":
                    json.put("temperature", modelA.getTemperature());
                    json.put("humidity", modelA.getHumidity());
                    json.put("light", modelA.isLight());
                    json.put("presence", modelA.isPresence());
                    json.put("music", modelA.isMusic());
                    json.put("alarm", modelA.isAlarm());
                    break;
                case "B":
                    json.put("temperature", modelB.getTemperature());
                    json.put("humidity", modelB.getHumidity());
                    json.put("light", modelB.isLight());
                    json.put("presence", modelB.isPresence());
                    json.put("music", modelB.isMusic());
                    json.put("alarm", modelB.isAlarm());
                    break;
                case "C":
                    json.put("temperature", modelC.getTemperature());
                    json.put("humidity", modelC.getHumidity());
                    json.put("light", modelC.isLight());
                    json.put("presence", modelC.isPresence());
                    json.put("music", modelC.isMusic());
                    json.put("alarm", modelC.isAlarm());
                    break;
            }
            json.put("date", new DateTime(new Date()));
        } catch (JSONException e) {
            Log.d("Main", "JSONException: " + e.getMessage());
        }
        return json;
    }
}
