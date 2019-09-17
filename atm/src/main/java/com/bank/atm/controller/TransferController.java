package com.bank.atm.controller;

import com.bank.atm.client.function.DecodeIso;
import com.bank.atm.service.impl.PaymentService;
import com.bank.atm.service.impl.TransferService;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("atm")
public class TransferController {
    private String result="";
    private TransferService transferService;
    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transferInquiry")
    public String transferInquiry(@RequestBody String message) throws Exception {
        System.out.println ("Message from client: "+message);
        result = transferService.transferInquiry (message);
        return result;
    }

    @PostMapping("/transfer")
    public String transfer(@RequestBody String message){
        System.out.println ("Message from client: "+message);

        result = transferService.transfer (message);
        return result;
    }

    @PostMapping("/switchingInquiry")
    public String switching(@RequestBody String message){
        System.out.println ("Message from client: "+message);
        result = transferService.switchingInquiry (message);
        return result;
    }

    @PostMapping("/switchingTransfer")
    public String switchingTransfer(@RequestBody String message){
        System.out.println ("Message from client: "+message);
        result = transferService.switchingTransfer (message);
        return result;
    }
}
