package com.kirandroid.bhojanforall.modals;

import java.io.Serializable;

public class Happy implements Serializable {

    private String name;
    private String occasion;
    private String email;
    private String phone;
    private String money;
    private String recip;
    private String address;
    private String date;

    public Happy() {
    }

    public Happy(String name, String occasion, String email, String phone, String money, String recip, String address, String date) {
        this.name = name;
        this.occasion = occasion;
        this.email = email;
        this.phone = phone;
        this.money = money;
        this.recip = recip;
        this.address = address;
        this.date = date;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getRecip() {
        return recip;
    }

    public void setRecip(String recip) {
        this.recip = recip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
