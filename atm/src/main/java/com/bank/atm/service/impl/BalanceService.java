package com.bank.atm.service.impl;

import com.bank.atm.client.console.AtmClient;
import com.bank.atm.client.function.BalanceIso;
import com.bank.atm.client.function.DecodeIso;
import com.bank.atm.model.Account;
import com.bank.atm.repository.AccountRepository;
import com.bank.atm.service.api.BalanceQuery;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.net.Socket;

@Service
public class BalanceService implements BalanceQuery {
    private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);
    private AccountRepository accountRepository;
    private Socket socket;
    private DecodeIso decodeIso = new DecodeIso ();

    @Autowired
    public BalanceService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public String findByAccNumber(String msg) {
        System.out.println ();
        ISOMsg isoMsg = decodeIso.parseISOMessage (msg);
        String AccountNumber = isoMsg.getString (2);
        String pinNumber = isoMsg.getString (52);
        String server = isoMsg.getString (62).substring (0,9);
        int port = Integer.parseInt (isoMsg.getString (62).substring (9));

        BalanceIso balanceIso = new BalanceIso ();
        Account account = accountRepository.findByAccnumber (AccountNumber);
        logger.info ("Fetch data from database by account number");
        String message="";
        if(account != null){
            logger.info ("Data fetch successfully");
            if(AccountNumber.equalsIgnoreCase (account.getAccnumber ()) && Integer.parseInt (pinNumber) == Integer.parseInt (account.getAccpin ())){
                message = balanceIso.buildResponseMessage (AccountNumber, "00", account.getAccbalance ());
            }else{
                logger.warn ("Fail to Authentication");
                message = balanceIso.buildResponseMessage (AccountNumber,"05",0);
            }
        }else {
            logger.warn ("Empty data");
            message = balanceIso.buildResponseMessage (AccountNumber,"05",0);
        }

        try{
            socket=new Socket (server,port);
            if(socket.isConnected()){
                logger.info ("Client connected to {} on port {}",socket.getInetAddress(), socket.getPort());
            }else{
                logger.warn ("Failed connect to client");
            }
            DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(message);
            dout.flush();
            dout.close ();
            socket.close ();
        }catch (Exception e){
            logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
        }
        return message;
    }
}
