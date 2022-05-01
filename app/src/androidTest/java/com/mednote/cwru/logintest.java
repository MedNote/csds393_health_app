package com.mednote.cwru;

import static org.junit.Assert.assertEquals;

import com.mednote.cwru.login.LoginViewModel;

import org.junit.Test;

public class logintest {

    private LoginViewModel loginViewModel = new LoginViewModel();

    @Test
    public void VMConstruct(){
        assertEquals("", loginViewModel.getWalletID());
        assertEquals("", loginViewModel.getMnemonic());
        assertEquals("", loginViewModel.getPassword());
    }

    @Test
    public void testSetters(){
        loginViewModel.setWalletID("1111111111");
        assertEquals("1111111111", loginViewModel.getWalletID());
        loginViewModel.setMnemonic("test test test test test test test test test test");
        assertEquals("test test test test test test test test test test", loginViewModel.getMnemonic());
        loginViewModel.setPassword("password");
        assertEquals("password", loginViewModel.getPassword());
    }

}
