package com.vn.quanly.api;

import android.os.AsyncTask;

public abstract class getAPI extends AsyncTask<Void,Void,Void> {
    public getAPI(String url,String token){
        this.token =token;
        this.url = url;
    }

    String url;
    String token;
    String result;
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            result = ModalAPI.getAPI(url,token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    public abstract void doPostExecute(String result);
}
