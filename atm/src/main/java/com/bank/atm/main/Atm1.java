package com.bank.atm.main;

import com.bank.atm.client.console.AtmClient;

public class Atm1 {
    public static void main(String[] args) throws Exception {
        AtmClient atm = new AtmClient ("localhost",6060);
        atm.main (new String[0]);
    }
}
