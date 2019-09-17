package com.bank.atm.service.impl;

import com.bank.atm.client.function.DecodeIso;
import com.bank.atm.client.function.PaymentIso;
import com.bank.atm.model.Account;
import com.bank.atm.repository.AccountRepository;
import com.bank.atm.service.api.PaymentQuery;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.net.Socket;

@Service
public class PaymentService implements PaymentQuery {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private AccountRepository accountRepository;
    private boolean status;
    private boolean isAccountExist;
    private String message;
    private ISOMsg isoMsg;
    private Socket socket;
    private DecodeIso decodeIso = new DecodeIso ();

    @Autowired
    public PaymentService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public String paymentInquiry(String msg) throws Exception {
        isoMsg = decodeIso.parseISOMessage (msg);
        String accountNumber = isoMsg.getString (2);
        String pinNumber = isoMsg.getString (52);
        int amount = Integer.parseInt (isoMsg.getString (4));
        String virtualAccount = isoMsg.getString (62);
        String server = isoMsg.getString (48).substring (0,9);
        int port = Integer.parseInt (isoMsg.getString (48).substring (9));

        Account account = accountRepository.findByAccnumber (accountNumber);
        logger.info ("Fetch data from database by account number {}",accountNumber);
        PaymentIso paymentIso = new PaymentIso ();
        Account paymentAccount = accountRepository.findByVirtualaccountdana (virtualAccount);
        logger.info ("Fetch data from database by forwarding account number {}",virtualAccount);
        if(account!=null){
            if(accountNumber.equalsIgnoreCase (account.getAccnumber ()) && Integer.parseInt (pinNumber) == Integer.parseInt (account.getAccpin ())){
                if(checkSisaSaldo (accountNumber, amount)){
                    if(isAccountExist (virtualAccount)){
                        message = paymentIso.responseIsoMessageInquiry("00", paymentAccount.getAccname (), account.getAccbalance ());
                        System.out.println (message);
                    }else{
                        logger.warn ("Virtual account does not exist");
                        message = paymentIso.responseIsoMessageInquiry ("76","Null", account.getAccbalance ());
                    }
                }else {
                    message = paymentIso.responseIsoMessageInquiry ("51","Null",account.getAccbalance ());
                }
            }else{
                logger.warn ("Failed to authentication");
                message = paymentIso.responseIsoMessageInquiry ("05",account.getAccname (), 0);
            }
        }else {
            logger.warn ("Data is empty");
            message = paymentIso.responseIsoMessageInquiry ("05","Null", 0);
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

    @Override
    public String payment(String msg) {
        isoMsg= decodeIso.parseISOMessage (msg);
        String accountNumber = isoMsg.getString (2);
        int amount = Integer.parseInt (isoMsg.getString (4));
        String server = isoMsg.getString (48).substring (0,9);
        int port = Integer.parseInt (isoMsg.getString (48).substring (9));

        PaymentIso paymentIso = new PaymentIso ();
        Account account = accountRepository.findByAccnumber (accountNumber);
        account.setAccbalance (account.getAccbalance ()-amount);
        accountRepository.save (account);
        Account newAccount = accountRepository.findByAccnumber (accountNumber);
        message = paymentIso.buildResponseMessage (accountNumber,"00",newAccount.getAccbalance ());
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

    private boolean isAccountExist(String virtualAccount) throws Exception{
        Account account = accountRepository.findByVirtualaccountdana (virtualAccount);
        if(account!=null){
            isAccountExist = true;
        }else{
            isAccountExist = false;
        }
        return isAccountExist;
    }

    private boolean checkSisaSaldo(String accountNumber, int nominal){
        Account account = accountRepository.findByAccnumber (accountNumber);
        if(account.getAccbalance ()<nominal){
            status=false;
        }else {
            status=true;
        }
        return status;
    }

}
