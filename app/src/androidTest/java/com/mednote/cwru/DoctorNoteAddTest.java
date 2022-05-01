package com.mednote.cwru;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DoctorNoteAddTest {
    private DoctorNoteAddViewModel doctorNoteAddViewModel = new DoctorNoteAddViewModel();

    @Test
    public void testGettersAndSetters(){
        doctorNoteAddViewModel.setAllergie("1111111111");
        assertEquals("1111111111", doctorNoteAddViewModel.getAllergie());
        doctorNoteAddViewModel.setNotes("test test test test test test test test test test");
        assertEquals("test test test test test test test test test test", doctorNoteAddViewModel.getNotes());
        doctorNoteAddViewModel.setDob("test test test test test test test test test test");
        assertEquals("test test test test test test test test test test", doctorNoteAddViewModel.getDob());
        doctorNoteAddViewModel.setImmunization("test test test test test test test test test test");
        assertEquals("test test test test test test test test test test", doctorNoteAddViewModel.getImmunization());
        doctorNoteAddViewModel.setName("1111111111");
        assertEquals("1111111111", doctorNoteAddViewModel.getName());
        doctorNoteAddViewModel.setMedication("password");
        assertEquals("password", doctorNoteAddViewModel.getMedication());
    }
}
