package com.bank.atm.controller;

import com.bank.atm.client.function.DecodeIso;
import com.bank.atm.service.impl.BalanceService;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("atm")
public class BalanceController {
    private DecodeIso decodeIso = new DecodeIso ();
    private BalanceService balanceService;
    private ISOMsg isoMsg;
    private String result="";
    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PostMapping(value = "/balanced")
    public String checkSaldo(@RequestBody String message){
        System.out.println ("Message from client: "+message);
        result = balanceService.findByAccNumber (message);
        System.out.println ("Pesan kepada client: "+result);
        return result;
    }
}
