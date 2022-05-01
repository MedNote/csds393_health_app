package com.mednote.cwru;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserTest {
    
    UserViewModel userViewModel = new UserViewModel();

    @Test public void VMConstruct(){
        assertEquals("", userViewModel.getUuid());
        assertEquals("", userViewModel.getName());
        assertEquals("", userViewModel.getDob());
    }

    @Test
    public void testSetters(){
        userViewModel.setUuid("123123123");
        assertEquals("123123123", userViewModel.getUuid());
        userViewModel.setName("Name");
        assertEquals("Name", userViewModel.getName());
        userViewModel.setDob("01/01/2000");
        assertEquals("01/01/2000", userViewModel.getDob());
    }
}
