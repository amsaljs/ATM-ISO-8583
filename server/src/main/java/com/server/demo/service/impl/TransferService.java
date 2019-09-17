package com.server.demo.service.impl;

import com.server.demo.functions.BuildIso;
import com.server.demo.functions.ClientHttp;
import com.server.demo.functions.DecodeIso;
import com.server.demo.model.Account;
import com.server.demo.repository.AccountRepository;
import com.server.demo.service.api.TransferQuery;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService implements TransferQuery {
    private AccountRepository accountRepository;
    private String result;
    private ISOMsg isoMsg;
    private DecodeIso decodeIso = new DecodeIso ();
    private ClientHttp clientHttp = new ClientHttp ();

    @Autowired
    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public String transferInquiry(String message) {
        BuildIso buildIso = new BuildIso ();
        isoMsg= decodeIso.parseISOMessage (message);
        String AccountNumber = isoMsg.getString (62);
        Account account = accountRepository.findByAccountnumber (AccountNumber);
        if(account != null){
            result =  buildIso.responseIsoMessageInquiry (isoMsg.getString (2),AccountNumber, "00",account.getAccountname (),isoMsg.getString (127));
        }else {
            result = buildIso.responseIsoMessageInquiry (isoMsg.getString (2),AccountNumber, "76","Null",isoMsg.getString (127));
        }
        return result;
    }

    @Override
    public String transfer(String message) {
        BuildIso buildIso = new BuildIso ();
        isoMsg= decodeIso.parseISOMessage (message);
        int amount = Integer.parseInt (isoMsg.getString (4));
        String AccountNumber = isoMsg.getString (62);
        System.out.println (AccountNumber);
        Account account = accountRepository.findByAccountnumber (AccountNumber);
        if(account != null){
            account.setAccountbalance (account.getAccountbalance () + amount);
            accountRepository.save (account);
            Account newAccount = accountRepository.findByAccountnumber (AccountNumber);
            result =  buildIso.responseIsoMessageInquiry (isoMsg.getString (2),AccountNumber,"00",newAccount.getAccountname (),isoMsg.getString (127));
        }else {
            result = buildIso.responseIsoMessageInquiry (isoMsg.getString (2),AccountNumber, "76","Null",isoMsg.getString (127));
        }
        return result;
    }
}
