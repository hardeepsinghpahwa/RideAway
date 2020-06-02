package com.pahwa.rideaway;

public class bookingdetails {

    String seats,uid;

    public bookingdetails(String seats, String uid) {
        this.seats = seats;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public bookingdetails() {
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }
}
