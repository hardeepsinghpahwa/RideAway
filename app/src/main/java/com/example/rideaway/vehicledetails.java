package com.example.rideaway;

public class vehicledetails {
    String vehiclename,vehiclenumber;

    public String getVehiclenumber() {
        return vehiclenumber;
    }

    public void setVehiclenumber(String vehiclenumber) {
        this.vehiclenumber = vehiclenumber;
    }

    public String getVehiclename() {
        return vehiclename;
    }

    public void setVehiclename(String vehiclename) {
        this.vehiclename = vehiclename;
    }

    public vehicledetails() {
    }

    public vehicledetails(String vehiclename, String vehiclenumber) {
        this.vehiclename = vehiclename;
        this.vehiclenumber = vehiclenumber;
    }
}
