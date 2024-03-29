package com.mednote.cwru;

import androidx.databinding.Bindable;

import com.mednote.cwru.base.BaseViewModel;

public class DoctorNoteAddViewModel extends BaseViewModel {

    private String name;
    private String dob;
    private String allergie;
    private String medication;
    private String immunization;
    private String notes;

    @Override
    protected void instantiatePermissions() {

    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public String getDob() {
        return dob;
    }

    @Bindable
    public String getAllergie() {
        return allergie;
    }

    @Bindable
    public String getMedication() {
        return medication;
    }

    @Bindable
    public String getImmunization() {
        return immunization;
    }

    @Bindable
    public String getNotes() {
        return notes;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public void setDob(String dob) {
        this.dob = dob;
        notifyPropertyChanged(BR.dob);
    }

    public void setAllergie(String allergie) {
        this.allergie = allergie;
        notifyPropertyChanged(BR.allergie);
    }

    public void setMedication(String medication) {
        this.medication = medication;
        notifyPropertyChanged(BR.medication);
    }

    public void setImmunization(String immunization) {
        this.immunization = immunization;
        notifyPropertyChanged(BR.immunization);
    }

    public void setNotes(String notes) {
        this.notes = notes;
        notifyPropertyChanged(BR.notes);
    }

}
