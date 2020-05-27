package com.vn.quanly.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class BillOfSale {
    String unit_price;
    String categories;
    String type;
    String quantity;
    String note;
    String unit;
    String date;
    String address;
    String id;
    public BillOfSale(String date,String categories, String type,String unit, String unit_price, String quantity,String note,String address){
        this.categories = categories;
        this.type = type;
        this.unit_price = unit_price;
        this.quantity = quantity;
        this.note = note;
        this.unit = unit;
        this.date = date;
        this.address = address;
    }
    public BillOfSale(String id,String date,String address,String categories, String type,String unit, String unit_price, String quantity,String note){
        this.categories = categories;
        this.type = type;
        this.unit_price = unit_price;
        this.quantity = quantity;
        this.note = note;
        this.unit = unit;
        this.date = date;
        this.address = address;
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public String getCategories() {
        return categories;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return isNumeric( quantity);
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getUnit_price() {
        return isNumeric(unit_price);
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return (unit);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String  isNumeric(String strNum) {
        if (strNum == null || !pattern.matcher(strNum).matches()||strNum.trim().equals("")) {
            return "0";
        }
        return strNum;
    }
}

