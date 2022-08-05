package com.kirandroid.bhojanforall.modals;

import java.io.Serializable;

public class RecipientsUpdate implements Serializable {

    private String don_name;
    private String vol_name;
    private String vol_phone;
    private String no_of_people;

    public RecipientsUpdate() {
    }

    public RecipientsUpdate(String don_name, String vol_name, String vol_phone, String no_of_people) {
        this.don_name = don_name;
        this.vol_name = vol_name;
        this.vol_phone = vol_phone;
        this.no_of_people = no_of_people;
    }


    public String getDon_name() {
        return don_name;
    }

    public void setDon_name(String don_name) {
        this.don_name = don_name;
    }

    public String getVol_name() {
        return vol_name;
    }

    public void setVol_name(String vol_name) {
        this.vol_name = vol_name;
    }

    public String getVol_phone() {
        return vol_phone;
    }

    public void setVol_phone(String vol_phone) {
        this.vol_phone = vol_phone;
    }

    public String getNo_of_people() {
        return no_of_people;
    }

    public void setNo_of_people(String no_of_people) {
        this.no_of_people = no_of_people;
    }
}
