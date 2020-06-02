package com.vn.quanly.model;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class Noitification {
    private String title;
    private String code,time, money;
    JSONObject data;
    private boolean isCheck;
    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    public Noitification(JSONObject data,String title,String code,boolean isCheck,String time,String money){
        this.isCheck = isCheck;
        this.data = data;
        this.title = title;
        this.code = code;
        this.time = time;
        this.money = money;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public JSONObject getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String status) {
        this.code = status;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean getCheck(){
        return isCheck;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMoney() {
        return isNumeric(money);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        if(time.equals("null")||time==null){
            return "Không hạn ngày";
        }
        return time;
    }
    public String  isNumeric(String strNum) {
        if (strNum == null || !pattern.matcher(strNum).matches()||strNum.trim().equals("")) {
            return "0";
        }
        return strNum;
    }
}
