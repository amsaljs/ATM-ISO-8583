package com.bank.atm.service.api;

public interface PaymentQuery {
    String paymentInquiry(String msg) throws Exception;
    String payment(String msg);
}
