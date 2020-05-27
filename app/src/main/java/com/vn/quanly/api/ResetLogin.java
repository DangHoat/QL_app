package com.vn.quanly.api;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.vn.quanly.ui.MainActivity;
import com.vn.quanly.ui.StartActivity;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;

import org.apache.poi.ss.formula.functions.T;
import org.json.JSONException;
import org.json.JSONObject;


public class ResetLogin extends AsyncTask<Void,Void,Void> {
    String result;
    Context context;
    AsyntaskAPI asyntaskAPI;
    String  token;
    public ResetLogin(Context context,AsyntaskAPI asyntaskAPI,String token) {
         this.context = context;
         this.asyntaskAPI = asyntaskAPI;
         this.token = token;
        }
    @Override
    protected Void doInBackground(Void... voids) {
        //do reloginpleaseReset https://immense-wave-34932.herokuapp.com/api/auth/refresh
        String newToken = null;
        try {
            newToken = ModalAPI.sendAPI(new JSONObject(), ConfigAPI.API_REFRESH_TOKEN,"POST",token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject newdata = null;
        try {
            newdata = new JSONObject(newToken);
            Log.e("pleaseRecallMe",newToken);
            if(!newToken.equals("")&&!newdata.getString("access_token").equals("")){
                new SaveDataSHP(context).SaveToken(newdata.getString("access_token"));
            }

//            else {
//                new SaveDataSHP(context).removeSHP();
//                context.startActivity(new Intent(context, StartActivity.class));
//                Toast.makeText(context,"Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
