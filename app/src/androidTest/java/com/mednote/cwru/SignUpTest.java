package com.mednote.cwru;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SignUpTest {

    private SignUpViewModel signUpViewModel = new SignUpViewModel();

    @Test public void VMConstruct(){
        assertEquals("", signUpViewModel.getFname());
        assertEquals("", signUpViewModel.getLname());
        assertEquals("", signUpViewModel.getDOB());
        assertEquals("", signUpViewModel.getPassword());
    }

    @Test
    public void testSetters(){
        signUpViewModel.setFname("First");
        assertEquals("First", signUpViewModel.getFname());
        signUpViewModel.setLname("Last");
        assertEquals("Last", signUpViewModel.getLname());
        signUpViewModel.setPassword("password");
        assertEquals("password", signUpViewModel.getPassword());
        signUpViewModel.setDOB("01/01/2000");
        assertEquals("01/01/2000", signUpViewModel.getDOB());

    }
}
