package com.example.communityd1.DataClasses;

public class BookingRequest {
    String date;
    String time;
    String purpose;
    String client;
    String roomId;
    String bookingId;
    String faculty;
    String reason;

    public BookingRequest(String date, String time, String purpose, String client, String roomId, String bookingId, String faculty,String reason) {
        this.date = date;
        this.time = time;
        this.purpose = purpose;
        this.client = client;
        this.roomId = roomId;
        this.bookingId = bookingId;
        this.faculty = faculty;
        this.reason = reason;
    }

    public String getReason() { return reason;}
    public void setReason() { this.reason = reason ;}

    public String getDate() {
        return date;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFaculty(){ return faculty; }

    public void setFaculty(String faculty){ this.faculty = faculty; }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getClient() {
        return client;
    }

    public String getRoomId() {
        return roomId;
    }
}
