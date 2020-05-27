package com.vn.quanly.model;

import java.util.ArrayList;

public class items {
    String categories;
    ArrayList<String> type;
    public items(String categories, ArrayList<String > type){
        this.categories = categories;
        this.type = type;
    }

    public String getCategories() {
        return categories;
    }

    public ArrayList<String> getType() {
        return type;
    }

    public void setType(ArrayList<String> type) {
        this.type = type;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
}
