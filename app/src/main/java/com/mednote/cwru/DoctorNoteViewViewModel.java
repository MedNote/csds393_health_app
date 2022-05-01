package com.mednote.cwru;

import android.widget.ArrayAdapter;

import androidx.databinding.Bindable;

import com.mednote.cwru.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class DoctorNoteViewViewModel extends BaseViewModel {

    private String name;
    private String dob;
    private String allergie;
    private String medication;
    private String immunization;
    private List<String> notes;
    private ArrayAdapter<String> notesAdapter;

    public DoctorNoteViewViewModel() {
        notes = new ArrayList<>();
    }

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
    public List<String> getNotes() {
        return notes;
    }

    @Bindable
    public ArrayAdapter<String> getNotesAdapter() {
        return notesAdapter;
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

    public void setNotes(List<String> notes) {
        this.notes = notes;
        notifyPropertyChanged(BR.notes);
    }

    public void setNotesAdapter(ArrayAdapter<String> notesAdapter) {
        this.notesAdapter = notesAdapter;
        notifyPropertyChanged(BR.notesAdapter);
    }
}
