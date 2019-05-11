package com.javier.bluetooth_hc06.util;

public class Room {

    private int temperature = 0;
    private int humidity = 0;
    private boolean light = false;
    private boolean presence = false;
    private boolean music = false;
    private boolean alarm = false;

    public Room() {}

    public Room(int temperature, int humidity, boolean light, boolean presence, boolean music, boolean alarm) {
        super();
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.presence = presence;
        this.music = music;
        this.alarm = alarm;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public boolean isLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public boolean isPresence() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence = presence;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }
}
