package com.mednote.cwru.login.models;

import java.io.Serializable;
import java.util.List;

public class AccountData implements Serializable {
    private final String uuid;
    private Name name;
    private String dob;
    private List<String> allergies;
    private List<String> medications;
    private List<Immunization> immunizations;
    private List<Note> notes;

    public AccountData(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public Name getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public List<String> getMedications() {
        return medications;
    }

    public List<Immunization> getImmunizations() {
        return immunizations;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public void setImmunizations(List<Immunization> immunizations) {
        this.immunizations = immunizations;
    }

    public void setMedications(List<String> medications) {
        this.medications = medications;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
