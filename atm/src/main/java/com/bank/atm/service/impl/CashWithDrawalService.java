package com.bank.atm.service.impl;

import com.bank.atm.client.function.BalanceIso;
import com.bank.atm.client.function.CashWithdrawalIso;
import com.bank.atm.client.function.DecodeIso;
import com.bank.atm.model.Account;
import com.bank.atm.repository.AccountRepository;
import com.bank.atm.service.api.CashWithDrawalQuery;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.net.Socket;

@Service
public class CashWithDrawalService implements CashWithDrawalQuery {
    private static final Logger logger = LoggerFactory.getLogger(CashWithDrawalService.class);
    private AccountRepository accountRepository;
    private boolean status;
    private Socket socket;
    private DecodeIso decodeIso = new DecodeIso ();

    @Autowired
    public CashWithDrawalService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public String tarikTunai(String msg) {
        ISOMsg isoMsg = decodeIso.parseISOMessage (msg);
        String accountNumber = isoMsg.getString (2);
        String pinNumber = isoMsg.getString (52);
        int nominal = Integer.parseInt (isoMsg.getString (4));
        String server = isoMsg.getString (62).substring (0,9);
        int port = Integer.parseInt (isoMsg.getString (62).substring (9));

        Account account = accountRepository.findByAccnumber (accountNumber);
        logger.info ("Fetch data from database by account number {}",accountNumber);
        CashWithdrawalIso cashWithdrawalIso = new CashWithdrawalIso ();
        String message="";
        if(account!=null){
            if(accountNumber.equalsIgnoreCase (account.getAccnumber ()) && Integer.parseInt (pinNumber) == Integer.parseInt (account.getAccpin ())){
                if(checkSisaSaldo (accountNumber, nominal)){
                    account.setAccbalance (account.getAccbalance ()-nominal);
                    accountRepository.save (account);
                    Account accountModelResponse = accountRepository.findByAccnumber (accountNumber);
                    message = cashWithdrawalIso.buildResponseMessage (accountNumber,"00" ,accountModelResponse.getAccbalance ());
                }else {
                    Account accountModelResponse = accountRepository.findByAccnumber (accountNumber);
                    message = cashWithdrawalIso.buildResponseMessage (accountNumber,"51", accountModelResponse.getAccbalance ());
                }
            }else{
                logger.warn ("Fail to authentication");
                message = cashWithdrawalIso.buildResponseMessage (accountNumber,"05", 0);
            }
        }else {
            logger.warn ("Data is empty");
            message = cashWithdrawalIso.buildResponseMessage (accountNumber, "05", 0);
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
