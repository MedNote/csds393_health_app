package com.mednote.cwru;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WalletTest {

    WalletViewModel walletViewModel = new WalletViewModel();

    @Test public void VMConstruct(){
        assertEquals("", walletViewModel.getWalletId());
        assertEquals("", walletViewModel.getFname());
        assertEquals("", walletViewModel.getLname());
        assertEquals("", walletViewModel.getRsaKey());
        assertEquals("", walletViewModel.getMnemonic());
    }

    @Test
    public void testSetters(){
        walletViewModel.setFname("First");
        assertEquals("First", walletViewModel.getFname());
        walletViewModel.setLname("Last");
        assertEquals("Last", walletViewModel.getLname());
        walletViewModel.setWalletId("1111111111");
        assertEquals("1111111111", walletViewModel.getWalletId());
        walletViewModel.setRsaKey("12345");
        assertEquals("12345", walletViewModel.getRsaKey());
        walletViewModel.setMnemonic("test test test test test test");
        assertEquals("test test test test test test", walletViewModel.getMnemonic());
    }
    
}
