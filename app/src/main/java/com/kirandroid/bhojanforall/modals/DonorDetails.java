package com.kirandroid.bhojanforall.modals;

import java.io.Serializable;

public class DonorDetails implements Serializable {

    private String Name;
    private String Email;
    private String Phone;
    private String address;
    private String type;

    public DonorDetails() {
    }

    public DonorDetails(String name, String email, String phone, String address, String type) {
        Name = name;
        Email = email;
        Phone = phone;
        this.address = address;
        this.type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
