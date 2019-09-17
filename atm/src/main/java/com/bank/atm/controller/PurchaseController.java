package com.bank.atm.controller;

import com.bank.atm.client.function.DecodeIso;
import com.bank.atm.service.impl.PurchaseService;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("atm")
public class PurchaseController {
    private String result="";
    private PurchaseService purchaseService;
    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping(value = "/purchaseInquiry")
    public String purchaseInquiry(@RequestBody String message) throws Exception {
        System.out.println ("Message from client: "+message);
        result = purchaseService.purchaseInquiry (message);
        return result;
    }

    @PostMapping(value = "/purchase")
    public String purchase(@RequestBody String message){
        System.out.println ("Message from client: "+message);
        result = purchaseService.purchase (message);

        return result;
    }
}
