package com.vn.quanly.model;

public class User {
    String code;
    String username;
    String email;
    String avartar;
    boolean isAdmin;
    public User(String username,String email,boolean isAdmin){
        this.avartar = "";
        this.email = email;
        this.username = username;
        this.isAdmin = isAdmin;
    }
    public User(String username,String email,String avartar,boolean isAdmin){
        this.username = username;
        this.email = email;
        this.avartar = avartar;
        this.isAdmin = isAdmin;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

    public String getAvartar() {
        return avartar;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getAdmin(){
        return isAdmin;
    }
}

