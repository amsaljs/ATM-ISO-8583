package com.switching.demo.controller;

import com.switching.demo.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {
    private TransferService transferService;
    private String result="";
    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }
    @PostMapping(value = "switchingInquiry")
    public String transferInquiry(@RequestBody String message){
        System.out.println ("Message from main Bank = "+message);
        result = transferService.transferInquiry (message);
        return result;
    }
    @PostMapping(value = "switchingTransfer")
    public String transfer(@RequestBody String message){
        System.out.println ("Message from main Bank = "+message);
        result = transferService.transfer (message);
        return result;
    }
}
