package com.pahwa.rideaway;

import com.pahwa.rideaway.Notification.Data;

import java.util.Date;

public class ridedetails {

    String from, to, seats, type, status, uid, userid, price;

    Date date;

    public ridedetails(String from, String to, Date date, String seats, String type, String status, String uid, String price) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.seats = seats;
        this.type = type;
        this.status = status;
        this.uid = uid;
        this.userid = userid;
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public ridedetails(String from, String to, Date date, String seats, String type, String status, String uid, String userid, String price) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.seats = seats;
        this.type = type;
        this.status = status;
        this.uid = uid;
        this.userid = userid;
        this.price = price;
    }

    public ridedetails() {
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
