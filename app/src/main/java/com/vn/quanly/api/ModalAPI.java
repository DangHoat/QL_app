package com.vn.quanly.api;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.ConfigMemory;
import com.vn.quanly.utils.ToolsCheck;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ModalAPI {
    public static String sendAPI(JSONObject data, String strurl, String strMethod, String token) throws Exception {
        URL url = null;
        try {
            url = new URL(strurl);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setRequestMethod(strMethod);
        urlConnection.setRequestProperty("Content-Type",
                "application/json;charset=utf-8");
        urlConnection.setRequestProperty("Accept","application/json");
//            urlConnection.setRequestProperty("Content-Length", "" +
//                    Integer.toString(requestBody.getBytes().length));
        urlConnection.setRequestProperty("Content-Language", "en-US");
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Authorization", "Bearer " + token);
        urlConnection.setRequestProperty("ApiKey", ConfigAPI.API_KEY);

        OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
        writer.write(data.toString());
        writer.flush();
        writer.close();
        outputStream.close();
        InputStream inputStream;

        // get stream
        if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            inputStream = urlConnection.getInputStream();
        } else {
            inputStream = urlConnection.getErrorStream();
        }
        // parse stream
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String temp, response = "";
        while ((temp = bufferedReader.readLine()) != null) {
            response += temp;
        }
        return response;
    }
    public static String getAPI(String URL,String token) throws Exception{
        URL url = null;
        try {
            url = new URL(URL);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            Log.e("ModalAPI",e1.toString());
        }
        HttpURLConnection urlConnection = null;
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("content-type","application/json");
        urlConnection.setRequestProperty("Accept","application/json");
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Authorization", "Bearer " + token);
        InputStream inputStream;

        // get stream
        if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            inputStream = urlConnection.getInputStream();
        } else {
            inputStream = urlConnection.getErrorStream();
        }
        // parse stream
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String temp, response = "";
        while ((temp = bufferedReader.readLine()) != null) {
            response += temp;
        }
        return response ;
    }
}
