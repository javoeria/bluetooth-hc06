package com.javier.bluetooth_hc06.util;

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
}
