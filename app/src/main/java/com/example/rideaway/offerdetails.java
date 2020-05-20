package com.example.rideaway;

import java.io.Serializable;

public class offerdetails implements Serializable {

    String pickupname,dropname,timeanddate,seats,price,instant,moreinfo,userid;
    double pickuplat, pickuplong, droplat,droplong;

    public String getPickupname() {
        return pickupname;
    }

    public void setPickupname(String pickupname) {
        this.pickupname = pickupname;
    }

    public String getDropname() {
        return dropname;
    }

    public void setDropname(String dropname) {
        this.dropname = dropname;
    }

    public String getTimeanddate() {
        return timeanddate;
    }

    public void setTimeanddate(String timeanddate) {
        this.timeanddate = timeanddate;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInstant() {
        return instant;
    }

    public void setInstant(String instant) {
        this.instant = instant;
    }

    public String getMoreinfo() {
        return moreinfo;
    }

    public void setMoreinfo(String moreinfo) {
        this.moreinfo = moreinfo;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public double getPickuplat() {
        return pickuplat;
    }

    public void setPickuplat(double pickuplat) {
        this.pickuplat = pickuplat;
    }

    public double getPickuplong() {
        return pickuplong;
    }

    public void setPickuplong(double pickuplong) {
        this.pickuplong = pickuplong;
    }

    public double getDroplat() {
        return droplat;
    }

    public void setDroplat(double droplat) {
        this.droplat = droplat;
    }

    public double getDroplong() {
        return droplong;
    }

    public void setDroplong(double droplong) {
        this.droplong = droplong;
    }

    public offerdetails() {
    }

    public offerdetails(String pickupname, String dropname, String timeanddate, String seats, String price, String instant, String moreinfo, String userid, double pickuplat, double pickuplong, double droplat, double droplong) {
        this.pickupname = pickupname;
        this.dropname = dropname;
        this.timeanddate = timeanddate;
        this.seats = seats;
        this.price = price;
        this.instant = instant;
        this.moreinfo = moreinfo;
        this.userid = userid;
        this.pickuplat = pickuplat;
        this.pickuplong = pickuplong;
        this.droplat = droplat;
        this.droplong = droplong;
    }
}
