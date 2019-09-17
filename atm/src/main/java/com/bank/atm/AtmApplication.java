package com.bank.atm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AtmApplication {
    private static final Logger logger = LoggerFactory.getLogger(AtmApplication.class);
    public static void main(String[] args) {
        SpringApplication.run (AtmApplication.class, args);

    }

}
