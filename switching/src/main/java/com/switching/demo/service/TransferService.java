package com.switching.demo.service;

import com.switching.demo.function.BuildIso;
import com.switching.demo.function.ClientHttp;
import com.switching.demo.function.DecodeIso;
import org.jpos.iso.ISOMsg;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private String result="";
    private ISOMsg isoMsg;
    private DecodeIso decodeIso = new DecodeIso ();
    private ClientHttp clientHttp = new ClientHttp ();
    private BuildIso buildIso = new BuildIso ();

    public String transferInquiry(String message){
        String url = "transferInquiry";
        isoMsg= decodeIso.parseISOMessage (message);
        String accountNumber = isoMsg.getString (2);
        int amount = Integer.parseInt (isoMsg.getString (4));
        String forwardingAccount = isoMsg.getString (62);
        String bankCode = isoMsg.getString (127);
        if(bankCode.equalsIgnoreCase ("003")){
            amount+=6500;
        }
        String messageToServer = buildIso.buildIsoMessage (accountNumber,bankCode,forwardingAccount,amount);
        System.out.println ("Message to other Server = "+messageToServer);
        String messageFromServer = clientHttp.sendHttp (messageToServer, url);
        isoMsg = decodeIso.parseISOMessage (messageFromServer);
        System.out.println ("Message from other server "+messageFromServer);
        result = buildIso.responseIsoMessage (accountNumber, forwardingAccount, isoMsg.getString (39), isoMsg.getString (103),isoMsg.getString (127), amount);

        return result;
    }

    public String transfer(String message){
        String url="transfer";
        isoMsg= decodeIso.parseISOMessage (message);
        String accountNumber = isoMsg.getString (2);
        int amount = Integer.parseInt (isoMsg.getString (4));
        String forwardingAccount = isoMsg.getString (62);
        String bankCode = isoMsg.getString (127);
        if(bankCode.equalsIgnoreCase ("003")){
            amount=amount-6500;
        }
        String messageToServer = buildIso.buildIsoMessage (accountNumber,bankCode,forwardingAccount,amount);
        String messageFromServer = clientHttp.sendHttp (messageToServer,url);
        isoMsg = decodeIso.parseISOMessage (messageFromServer);
        result = buildIso.responseIsoMessage(accountNumber,forwardingAccount,isoMsg.getString (39), isoMsg.getString (103),isoMsg.getString (127),amount);

        return result;
    }
}
