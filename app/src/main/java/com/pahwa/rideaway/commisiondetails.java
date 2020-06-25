package com.pahwa.rideaway;

public class commisiondetails {

    String from, to, date, totalprice,seats;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public commisiondetails() {
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public commisiondetails(String from, String to, String date, String totalprice, String seats) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.totalprice = totalprice;
        this.seats = seats;
    }
}
