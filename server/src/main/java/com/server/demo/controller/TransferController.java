package com.server.demo.controller;

import com.server.demo.service.impl.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("atm")
public class TransferController {
    private TransferService transferService;
    private String result="";
    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping(value = "transferInquiry")
    public String transferInquiry(@RequestBody String message){
        System.out.println ("Message from Switching = "+message);
        result = transferService.transferInquiry (message);
        return result;
    }

    @PostMapping(value = "transfer")
    public String transfer(@RequestBody String message){
        System.out.println ("Message from Switching = "+message);
        result = transferService.transfer (message);
        return result;
    }
}
