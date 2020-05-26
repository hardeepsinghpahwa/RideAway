package com.example.rideaway;

public class ridedetails {

    String from,to,time,seats,type,status;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ridedetails(String from, String to, String time, String seats, String type, String status) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.seats = seats;
        this.type = type;
        this.status = status;
    }

    public ridedetails() {
    }
}
