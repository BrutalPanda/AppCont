package com.sample.drawer.utils;

/**
 * Created by Slava-laptop on 29.07.2015.
 */
public class Order {

    private String address;
    private String FIO;
    private String account;

    public Order (String _address, String _FIO, String _account){
        this.address = _address;
        this.FIO = _FIO;
        this.account = _account;
    }

    public String getFIO() {
        return FIO;
    }
    public String getAccount(){return account;}
    public void setFIO(String orderName) {
        this.FIO = orderName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String orderStatus) {
        this.address = orderStatus;
    }
}