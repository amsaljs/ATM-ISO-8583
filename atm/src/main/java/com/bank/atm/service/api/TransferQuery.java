package com.bank.atm.service.api;

public interface TransferQuery {
    String transferInquiry(String message) throws Exception;
    String transfer(String message);
    String switchingInquiry(String message);
    String switchingTransfer(String message);


}
