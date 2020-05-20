package com.example.rideaway;

public class profiledetails {
    String name,phone,gender,birthday,image;

    public profiledetails(String name, String phone, String gender, String birthday, String image) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.birthday = birthday;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public profiledetails() {
    }
}
