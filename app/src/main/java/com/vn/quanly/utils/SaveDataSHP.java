package com.vn.quanly.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.vn.quanly.ui.StartActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class SaveDataSHP extends Activity {
    Context context;
    public static final String SHP ="AUTH";
    public static final String SHP_MEM ="MEMORY";
    public static final String SHP_EMAI ="EMAIL";
    public static final String SHP_PASS ="PASSWORD";
    public static final String SHP_TOKEN ="TOKEN";
    public static final String SHP_NAME ="NAME";
    public static final String SHP_PROMISE ="PROMISE";
    public static final String SHP_ADDRESS ="ADDRESS";
    public static final String SHP_TELECOM ="TELECOM";
    public static final String SHP_HOUR ="HOUR";
    public static final String SHP_MIN ="MIN";
    public static final String SHP_TYPE ="TYPE";
    public static final String SHP_TIME ="TIME";
    public static final String SHP_CATE ="CATE";
    public static final String SHP_UNIT ="UNIT";


    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesMemory;
    public SaveDataSHP(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHP,MODE_PRIVATE);
        sharedPreferencesMemory = context.getSharedPreferences(SHP_MEM,MODE_PRIVATE);
    }
    public String getString(String key){
        return sharedPreferences.getString(key,"");
    }

    public void SaveToken(String token){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHP_TOKEN,token);
        editor.commit();
    }
    public String getShpToken(){
        return sharedPreferences.getString(SHP_TOKEN,"");
    }

    public void SaveAuth(String email,String password){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHP_EMAI,email);
        editor.putString(SHP_PASS,password);
        editor.commit();
    }
    public boolean getAuth(){
        return !sharedPreferences.getString(SHP_EMAI,"").equals("") && !sharedPreferences.getString(SHP_PASS,"").equals("");
    }
    public JSONObject getInfo(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SHP_NAME,sharedPreferences.getString(SHP_NAME,""));
            jsonObject.put(SHP_EMAI,sharedPreferences.getString(SHP_EMAI,""));
            jsonObject.put(SHP_ADDRESS,sharedPreferences.getString(SHP_ADDRESS,""));
            jsonObject.put(SHP_TELECOM,sharedPreferences.getString(SHP_TELECOM,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public void setInfo(JSONObject data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString(SHP_EMAI,data.getString("email"));
            editor.putString(SHP_NAME,data.getString("name"));
            editor.putString(SHP_PROMISE,data.getString("permissions"));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public boolean SHP_Check(){
        try {
            String email = sharedPreferences.getString(SHP_EMAI,"");
            String password = sharedPreferences.getString(SHP_PASS,"");
            if(!email.equals("")&& !password.equals("")){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
    public int[] getTime(){
        int hour = sharedPreferences.getInt(SHP_HOUR,9);
        int min = sharedPreferences.getInt(SHP_MIN,0) ;
        int times = sharedPreferences.getInt(SHP_TIME,2) ;
        int[] arr ={hour,min,times};
        return arr;
    }
    public void setTime(int hour,int min,int time){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHP_HOUR,hour);
        editor.putInt(SHP_MIN,min);
        editor.putInt(SHP_TIME,time);
        editor.commit();
    }


    public void removeSHP(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void setSHP(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }


    public String getData(String type){
        return sharedPreferences.getString(type,"");
    }

    public String getType(){
        return sharedPreferencesMemory.getString(SHP_TYPE,"trắng nhân tạo,kim sa trung,trắng vân mây");
    }
    public void setListType(String type){
        SharedPreferences.Editor editor = sharedPreferencesMemory.edit();
        editor.putString(SHP_TYPE,type);
        editor.commit();
    }

    public String getUnit(){
        return sharedPreferencesMemory.getString(SHP_UNIT,"cái,m2,chiếc");
    }
    public void setListUnit(String unit){
        SharedPreferences.Editor editor = sharedPreferencesMemory.edit();
        editor.putString(SHP_UNIT,unit);
        editor.commit();
    }

    public String getCate(){
        return sharedPreferencesMemory.getString(SHP_CATE,"bàn bếp,lavabo,cầu thang");
    }
    public void setListCate(String cate){
        SharedPreferences.Editor editor = sharedPreferencesMemory.edit();
        editor.putString(SHP_CATE,cate);
        editor.commit();
    }




    public void setData(String type,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(type,value);
        editor.commit();
    }

    public void setClient(JSONObject data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString("code_client",data.getString("code"));
            editor.putString("name_client",data.getString("name"));
            editor.putString("address_client",data.getString("address"));
            editor.putString("telephone_client",data.getString("telephone"));
            editor.putString("status_client",data.getString("status"));
            if(data.getString("date_limit").equals("null")){
                editor.putString("date_limit_client","Không hạn ngày");
            }else {
                editor.putString("date_limit_client",data.getString("date_limit"));
            }

            if(data.getString("money_limit").equals("null")){
                editor.putString("money_limit_client","Hạn mức tối đa");
            }else {
                editor.putString("money_limit_client",data.getString("money_limit"));
            }
            editor.putString("total_client",data.getString("total"));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
