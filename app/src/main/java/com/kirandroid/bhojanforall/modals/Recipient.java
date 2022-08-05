package com.kirandroid.bhojanforall.modals;

import java.io.Serializable;

public class Recipient implements Serializable {

    private String org_name;
    private String type;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String category;

    public Recipient() {
    }

    public Recipient(String org_name, String type, String email, String password, String phone, String address, String category) {
        this.org_name = org_name;
        this.type = type;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.category = category;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
