package com.bank.atm.controller;

import com.bank.atm.broker.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("atm")
public class ProducerController {
    private Producer producer;
    @Autowired
    public ProducerController(Producer producer) {
        this.producer = producer;
    }

    @PostMapping(value = "/send")
    public String sendMsg(@RequestBody String message){
        producer.produceMsg (message);
        return "Done";
    }

}
