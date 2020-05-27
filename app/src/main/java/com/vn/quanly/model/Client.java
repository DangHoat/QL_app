package com.vn.quanly.model;

public class Client {
    String code;
    String name;
    String address;
    String telecom;
    public Client(String code,String name, String address, String telecom){
        this.code = code;
        this.name = name;
        this.address = address;
        this.telecom = telecom;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTelecom(String telecom) {
        this.telecom = telecom;
    }

    public String getTelecom() {
        return telecom;
    }
}

