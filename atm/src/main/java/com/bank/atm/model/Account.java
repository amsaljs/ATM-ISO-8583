package com.bank.atm.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Account {
    @Id
    private int id;
    private String accnumber;
    private String accpin;
    private String accname;
    private int accbalance;
    private String virtualaccountdana;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccnumber() {
        return accnumber;
    }

    public void setAccnumber(String accnumber) {
        this.accnumber = accnumber;
    }

    public String getAccpin() {
        return accpin;
    }

    public void setAccpin(String accpin) {
        this.accpin = accpin;
    }

    public String getAccname() {
        return accname;
    }

    public void setAccname(String accname) {
        this.accname = accname;
    }

    public int getAccbalance() {
        return accbalance;
    }

    public void setAccbalance(int accbalance) {
        this.accbalance = accbalance;
    }

    public String getVirtualaccountdana() {
        return virtualaccountdana;
    }

    public void setVirtualaccountdana(String virtualaccountdana) {
        this.virtualaccountdana = virtualaccountdana;
    }
}
