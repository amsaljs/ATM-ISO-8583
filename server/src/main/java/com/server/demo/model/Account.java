package com.server.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Account {
    @Id
    private int idaccount;
    private String accountnumber;
    private String accountname;
    private String accountpin;
    private int accountbalance;

    public int getIdaccount() {
        return idaccount;
    }

    public void setIdaccount(int idaccount) {
        this.idaccount = idaccount;
    }

    public String getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getAccountpin() {
        return accountpin;
    }

    public void setAccountpin(String accountpin) {
        this.accountpin = accountpin;
    }

    public int getAccountbalance() {
        return accountbalance;
    }

    public void setAccountbalance(int accountbalance) {
        this.accountbalance = accountbalance;
    }
}
