package com.example.communityd1.DataClasses;

public class Room {
    String name;
    String capacity;
    String roomId;

    public Room(String name, String capacity, String roomId) {
        this.name = name;
        this.capacity = capacity;
        this.roomId = roomId;
    }

    public String getId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public String getCapacity() {
        return capacity;
    }

}
