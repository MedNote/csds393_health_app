package com.mednote.cwru.login.models;

import java.io.Serializable;

public class Immunization implements Serializable {
    private String immunization;
    private String date;

    public Immunization(String immunization, String date) {
        this.immunization = immunization;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getImmunization() {
        return immunization;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImmunization(String immunization) {
        this.immunization = immunization;
    }

    @Override
    public String toString() {
        return "immunization='" + immunization + '\'' +
                ", date='" + date;
    }
}
