package com.vn.quanly.model;

public class Client {
    String code;
    String name;
    String address;
    String telecom;
    String total;
    int id;
    public Client(String code,String name, String address, String telecom,String total){
        this.code = code;
        this.name = name;
        this.address = address;
        this.telecom = telecom;
        this.total = total;
    }
    public Client(int id,String code,String name, String address, String telecom,String total){
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.telecom = telecom;
        this.total = total;
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

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotal() {
        return total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

