package com.bank.atm.controller;

import com.bank.atm.client.function.DecodeIso;
import com.bank.atm.service.impl.PaymentService;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("atm")
public class PaymentController {
    private String result="";
    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/inquiry")
    public String paymentInquiry(@RequestBody String message) throws Exception {
        System.out.println ("Message from client: "+message);

        result = paymentService.paymentInquiry (message);
        return result;
    }

    @PostMapping(value = "/payment")
    public String payment(@RequestBody String message){
        System.out.println ("Message from client: "+message);
        result = paymentService.payment (message);

        return result;
    }
}
