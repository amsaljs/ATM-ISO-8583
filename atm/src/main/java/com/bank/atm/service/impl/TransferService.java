package com.bank.atm.service.impl;

import com.bank.atm.client.function.ClientHttp;
import com.bank.atm.client.function.DecodeIso;
import com.bank.atm.client.function.TransferIso;
import com.bank.atm.client.function.TransferToOtherIso;
import com.bank.atm.model.Account;
import com.bank.atm.repository.AccountRepository;
import com.bank.atm.service.api.TransferQuery;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.net.Socket;

@Service
public class TransferService implements TransferQuery {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
    private AccountRepository accountRepository;
    private boolean status;
    private boolean isAccountExist;
    private String result;
    private ISOMsg isoMsg;
    private Socket socket;
    private DecodeIso decodeIso = new DecodeIso ();
    private ClientHttp clientHttp = new ClientHttp ();

    @Autowired
    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public String transferInquiry(String message) throws Exception {
        isoMsg= decodeIso.parseISOMessage (message);
        String accountNumber = isoMsg.getString (2);
        int amount = Integer.parseInt (isoMsg.getString (4));
        String pinNumber = isoMsg.getString (52);
        String destinationAccountNumber = isoMsg.getString (62);
        String server = isoMsg.getString (48).substring (0,9);
        int port = Integer.parseInt (isoMsg.getString (48).substring (9));

        Account account = accountRepository.findByAccnumber (accountNumber);
        logger.info ("Fetch data from database by account number {}",accountNumber);
        TransferIso transferIso = new TransferIso ();
        Account forwardingAccount = accountRepository.findByAccnumber (destinationAccountNumber);
        if(account!=null){
            if(accountNumber.equalsIgnoreCase (account.getAccnumber ()) && Integer.parseInt (pinNumber) == Integer.parseInt (account.getAccpin ())){
                if(checkSisaSaldo (accountNumber, amount)){
                    if(isAccountExist (destinationAccountNumber)){
                        result = transferIso.responseIsoMessageInquiry("00", forwardingAccount.getAccname (), account.getAccbalance ());
                    }else{
                        logger.warn ("Account Number does not exit");
                        result = transferIso.responseIsoMessageInquiry ("76","Null", account.getAccbalance ());
                    }
                }else {
                    result = transferIso.buildResponseMessage (accountNumber,"51", account.getAccbalance ());
                }
            }else{
                logger.warn ("Fail to authentication");
                result = transferIso.responseIsoMessageInquiry ("05",account.getAccname (), 0);
            }
        }else {
            logger.warn ("Data is empty");
            result = transferIso.buildResponseMessage (accountNumber, "05", 0);
        }
        try{
            socket=new Socket (server,port);
            if(socket.isConnected()){
                logger.info ("Client connected to {} on port {}",socket.getInetAddress(), socket.getPort());
            }else{
                logger.warn ("Failed connect to client");
            }
            DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(result);
            dout.flush();
            dout.close ();
            socket.close ();
        }catch (Exception e){
            logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
        }

        return result;
    }

    @Override
    public String transfer(String message) {
        isoMsg= decodeIso.parseISOMessage (message);
        String accountNumber = isoMsg.getString (2);
        int amount = Integer.parseInt (isoMsg.getString (4));
        String destinationAccountNumber = isoMsg.getString (62);
        String server = isoMsg.getString (48).substring (0,9);
        int port = Integer.parseInt (isoMsg.getString (48).substring (9));

        TransferIso transferIso = new TransferIso ();
        Account accountPrimer = accountRepository.findByAccnumber (accountNumber);
        accountPrimer.setAccbalance (accountPrimer.getAccbalance ()-amount);
        accountRepository.save (accountPrimer);
        logger.info ("Save data");
        addBalanceForwarding (destinationAccountNumber, amount);
        Account newAccount = accountRepository.findByAccnumber (accountNumber);
        result = transferIso.buildResponseMessage (accountNumber,"00",newAccount.getAccbalance ());

        try{
            socket=new Socket (server,port);
            if(socket.isConnected()){
                logger.info ("Client connected to {} on port {}",socket.getInetAddress(), socket.getPort());
            }else{
                logger.warn ("Failed connect to client");
            }
            DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(result);
            dout.flush();
            dout.close ();
            socket.close ();
        }catch (Exception e){
            logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
        }

        return result;
    }

    @Override
    public String switchingInquiry(String message) {
        String url = "switchingInquiry";
        String responseFromSwitching="";
        isoMsg= decodeIso.parseISOMessage (message);
        String accountNumber = isoMsg.getString (2);
        int amount = Integer.parseInt (isoMsg.getString (4));
        String pinNumber = isoMsg.getString (52);
        String server = isoMsg.getString (48).substring (0,9);
        int port = Integer.parseInt (isoMsg.getString (48).substring (9));

        responseFromSwitching = clientHttp.sendIsoToSwitching (message, url);
        isoMsg = decodeIso.parseISOMessage (responseFromSwitching);
        String statusCode = isoMsg.getString (39);
        String forwardingName = isoMsg.getString (103);
        int newAmount = Integer.parseInt (isoMsg.getString (4));
        Account account = accountRepository.findByAccnumber (accountNumber);
        TransferToOtherIso transferIso = new TransferToOtherIso ();
        if(account!=null){
            if(accountNumber.equalsIgnoreCase (account.getAccnumber ()) && Integer.parseInt (pinNumber) == Integer.parseInt (account.getAccpin ())){
                if(checkSisaSaldo (accountNumber, amount)){
                    if(statusCode.equalsIgnoreCase ("00")){
                        result = transferIso.responseIsoMessageInquiry(statusCode, forwardingName, isoMsg.getString (39),newAmount);
                        System.out.println (result);
                    }else{
                        result = transferIso.responseIsoMessageInquiry ("76","Null", isoMsg.getString (39),newAmount);
                    }
                }else {
                    result = transferIso.responseIsoMessageInquiry ("51","Null", isoMsg.getString (39),account.getAccbalance ());
                }
            }else{
                result = transferIso.responseIsoMessageInquiry ("05","Null", isoMsg.getString (39),0);
            }
        }else {
            result = transferIso.responseIsoMessageInquiry ("05","Null", isoMsg.getString (39),0);
        }

        try{
            socket=new Socket (server,port);
            if(socket.isConnected()){
                logger.info ("Client connected to {} on port {}",socket.getInetAddress(), socket.getPort());
            }else{
                logger.warn ("Failed connect to client");
            }
            DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(result);
            dout.flush();
            dout.close ();
            socket.close ();
        }catch (Exception e){
            logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
        }

        return result;
    }

    @Override
    public String switchingTransfer(String message) {
        String url = "switchingTransfer";
        String responseFromSwitching="";
        isoMsg= decodeIso.parseISOMessage (message);
        String accountNumber = isoMsg.getString (2);
        int amount = Integer.parseInt (isoMsg.getString (4));
        String server = isoMsg.getString (48).substring (0,9);
        int port = Integer.parseInt (isoMsg.getString (48).substring (9));

        responseFromSwitching = clientHttp.sendIsoToSwitching (message, url);
        isoMsg = decodeIso.parseISOMessage (responseFromSwitching);
        Account account = accountRepository.findByAccnumber (accountNumber);
        account.setAccbalance (account.getAccbalance ()-amount);
        accountRepository.save (account);
        Account newAccount = accountRepository.findByAccnumber (accountNumber);
        TransferToOtherIso transferIso = new TransferToOtherIso ();
        result = transferIso.buildResponseMessage (accountNumber,"00", isoMsg.getString (127),newAccount.getAccbalance ());

        try{
            socket=new Socket (server,port);
            if(socket.isConnected()){
                logger.info ("Client connected to {} on port {}",socket.getInetAddress(), socket.getPort());
            }else{
                logger.warn ("Failed connect to client");
            }
            DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(result);
            dout.flush();
            dout.close ();
            socket.close ();
        }catch (Exception e){
            logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
        }

        return result;
    }

    public void addBalanceForwarding(String destinationAccountNumber, int amount){
        Account accountForwarding = accountRepository.findByAccnumber (destinationAccountNumber);
        accountForwarding.setAccbalance (accountForwarding.getAccbalance ()+amount);
        accountRepository.save (accountForwarding);
    }

    private boolean isAccountExist(String destinationAccountNumber) throws Exception{
        Account account = accountRepository.findByAccnumber (destinationAccountNumber);
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
