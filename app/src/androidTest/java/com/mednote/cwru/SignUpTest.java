package com.mednote.cwru;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SignUpTest {

    private SignUpViewModel signUpViewModel = new SignUpViewModel();

    @Test void VMConstruct(){
        assertEquals("", signUpViewModel.getFname());
        assertEquals("", signUpViewModel.getLname());
        assertEquals("", signUpViewModel.getDOB());
        assertEquals("", signUpViewModel.getPassword());
    }

    @Test
    public void testSetters(){
        signUpViewModel.setFname("First");
        assertEquals("First", signUpViewModel.getFname());
        signUpViewModel.setFname("Last");
        assertEquals("Last", signUpViewModel.getLname());
        signUpViewModel.setFname("password");
        assertEquals("password", signUpViewModel.getPassword());
        signUpViewModel.setFname("01/01/2000");
        assertEquals("01/01/2000", signUpViewModel.getDOB());

    }
}
