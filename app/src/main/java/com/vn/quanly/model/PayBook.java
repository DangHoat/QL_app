package com.vn.quanly.model;

public class PayBook {
    private String customer;
    private String status;
    private String monney_limit;
    private String codeClient;
    private String date_limit;
    private String address;
    private String telephone;
    private String total;
    private boolean isPay;
    public PayBook(String customer, String status, String monney_limit, String date_limit,String codeClient){
        this.date_limit = date_limit;
        this.customer = customer;
        this.status = status;
        this.monney_limit = monney_limit;
        this.codeClient = codeClient;
        this.address ="";
        this.telephone = "";
        this.total = "";
        this.isPay = true;
    }
    public PayBook(String customer,String codeClient, String status, String monney_limit, String date_limit,String address,String telephone,String total,boolean isPay){
        this.date_limit = date_limit;
        this.customer = customer;
        this.status = status;
        this.monney_limit = monney_limit;
        this.codeClient = codeClient;
        this.address =address;
        this.telephone = telephone;
        this.total = total;
        this.isPay = isPay;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomer() {
        return customer;
    }

    public void setDate_limit(String date_limit) {

        this.date_limit = date_limit;
    }

    public String getDate_limit() {
        if(date_limit.equals("null")|| date_limit == null){
            date_limit = "Không giới hạn thời gian";
        }
        return date_limit;
    }

    public void setMonney_limit(String monney_limit) {

        this.monney_limit = monney_limit;
    }

    public String getMonney_limit() {
        if(monney_limit == "null"|| monney_limit ==null){
            return  "Không hạn mức tài chính";
        }
        return monney_limit;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setPay(boolean pay) {
        isPay = pay;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
    public Boolean getIsPay() {
        return isPay;
    }

    public void setIsPay(boolean isPay) {
        this.isPay = isPay;
    }
}


