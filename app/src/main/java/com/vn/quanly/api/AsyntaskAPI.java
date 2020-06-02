package com.vn.quanly.api;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.vn.quanly.R;
import com.vn.quanly.ui.StartActivity;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AsyntaskAPI extends AsyncTask<String,Void,Void> {
    JSONObject data;
    JSONObject username = new JSONObject();
    String URL;
    String method;
    String result;
    ProgressDialog progressDialog;
    Context context;
    String token;
    AsyntaskAPI asynTask = null;
    boolean isShowLoading = true;
    private boolean pleaseReset = true;
    public AsyntaskAPI(Context context,JSONObject data,String URL,String method){
        this.context = context;
        this.data  = data;
        this.URL = URL;
        this.method = method;
        this.token = "";
    }
    public AsyntaskAPI(Context context,JSONObject data,String URL,String method,String token){
        this.context = context;
        this.data  = data;
        this.URL = URL;
        this.method = method;
        this.token = token;
    }
    public AsyntaskAPI(Context context,String URL,String token,boolean isShowLoading){
        this.context = context;
        this.URL = URL;
        this.method = "get";
        this.token = token;
        this.isShowLoading = isShowLoading;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SaveDataSHP saveDataSHP = new SaveDataSHP(context);
        try {
            username.put("email",saveDataSHP.getString(SaveDataSHP.SHP_EMAI));
            username.put("password",saveDataSHP.getString(SaveDataSHP.SHP_EMAI));

        }catch (JSONException e){}
        progressDialog = new ProgressDialog(context);
        if(isShowLoading) {
            progressDialog.setMessage("Vui lòng chờ...");
            progressDialog.show();
        }
        this.setOnPreExcute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(progressDialog.isShowing()){
            progressDialog.cancel();
        }
        new SaveDataSHP(context).SaveToken(token);
        if(pleaseReset){
            this.setOnPostExcute(result);
        }else {
            new SaveDataSHP(context).removeSHP();
            context.startActivity(new Intent(context, StartActivity.class));
            Toast.makeText(context,"Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected Void doInBackground(String... strings) {
        Log.e("URL",URL);
        pleaseReset = true;
        try {
            if(method.toLowerCase().equals("get")){
                result = ModalAPI.getAPI(URL,token);
            }else {
                result = ModalAPI.sendAPI(data,URL,method,token);
            }
            Log.e("res co token",result);
//            {"message":"Unauthenticated."}
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AsyntaskAPILò",e.toString());
        }

        try {
            JSONObject check = new JSONObject(result);
            try {
                if(check.getString("message").trim().equals("Unauthenticated.")){
                    //do reloginpleaseReset https://immense-wave-34932.herokuapp.com/api/auth/refresh
                    String newToken = ModalAPI.sendAPI(username, ConfigAPI.API_LOGIN,"POST",token);
                    JSONObject newdata = new JSONObject(newToken);
                    Log.e("newToken",newToken);
                    if(!newdata.equals("")){
                        if (newdata.get("message").toString().equals("Successfully")) {
                            setToken(newdata.get("token").toString());
                            try {
                                if(method.toLowerCase().equals("get")){
                                    result = ModalAPI.getAPI(URL,token);
                                }else {
                                    result = ModalAPI.sendAPI(data,URL,method,token);
                                }
                                Log.e(" result",result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("AsyntaskAPILò",e.toString());
                            }
                        }else {
                            pleaseReset = false;
                        }
                    }
                    else {
                        pleaseReset = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("refresh",e.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(progressDialog.isShowing()) progressDialog.cancel();
        return null;
    }

    public void setToken(String token){
        this.token = token;
    }
    public String getToken() {
        return token;
    }

    public void setShowLoading(boolean showLoading) {
        isShowLoading = showLoading;
    }
    public abstract void setOnPreExcute();
    public abstract void setOnPostExcute(String JsonResult);
}
