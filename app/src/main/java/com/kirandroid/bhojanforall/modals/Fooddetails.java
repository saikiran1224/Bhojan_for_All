package com.kirandroid.bhojanforall.modals;

import java.io.Serializable;

public class Fooddetails  implements Serializable {

    private String name;
    private String phone;
    private String address;
    private String place;
    private String status;
    private String time;
    private String date;
    private String no_of_people;


    public Fooddetails() {
    }


    public Fooddetails(String name, String phone, String address, String place, String status, String time, String date, String no_of_people) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.place = place;
        this.status = status;
        this.time = time;
        this.date = date;
        this.no_of_people = no_of_people;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNo_of_people() {
        return no_of_people;
    }

    public void setNo_of_people(String no_of_people) {
        this.no_of_people = no_of_people;
    }
}
