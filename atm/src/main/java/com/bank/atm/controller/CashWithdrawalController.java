package com.bank.atm.controller;

import com.bank.atm.client.function.DecodeIso;
import com.bank.atm.service.impl.CashWithDrawalService;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("atm")
public class CashWithdrawalController {
    private String result="";
    private CashWithDrawalService cashWithDrawalService;
    @Autowired
    public CashWithdrawalController(CashWithDrawalService cashWithDrawalService) {
        this.cashWithDrawalService = cashWithDrawalService;
    }

    @PostMapping(value = "/cashwithdrawal")
    public String tarikTunai(@RequestBody String message) throws Exception{
        System.out.println ("Message from client: "+message);

        result = cashWithDrawalService.tarikTunai (message);
        System.out.println ("Pesan kepada client: "+result);
        return result;
    }
}
