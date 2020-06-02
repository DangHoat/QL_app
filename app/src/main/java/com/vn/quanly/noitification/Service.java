package com.vn.quanly.noitification;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vn.quanly.R;
import com.vn.quanly.adapter.ItemNoitifications;
import com.vn.quanly.adapter.ItemPayBooks;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.api.getAPI;
import com.vn.quanly.model.Noitification;
import com.vn.quanly.model.PayBook;
import com.vn.quanly.ui.MainActivity;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.vn.quanly.noitification.NotificationUI.CHANNEL_1;

public class Service extends android.app.Service {
    private NotificationManagerCompat notificationManagerCompat;
    private boolean isKill;
    SimpleDateFormat sdf;
    private  String token;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

    @Override
    public void onCreate() {
        super.onCreate();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        notificationManagerCompat = NotificationManagerCompat.from(this);
        isKill =true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//        String date = df.format(Calendar.getInstance().getTime());
//        int[] t = new SaveDataSHP(getApplicationContext()).getTime();
        token = intent.getStringExtra("token");
        if(ToolsCheck.checkInternetConnection(getApplicationContext())){
            new getNoi(token).execute();
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(),"onBind",Toast.LENGTH_LONG).show();
        return null;
    }
    public void setNotification(int id,String title,String description,String bigText){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification =new NotificationCompat.Builder(this, CHANNEL_1)
                .setSmallIcon(R.drawable.logo2)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigText))
                .setSound(alarmSound)
                .build();
        notificationManagerCompat.notify(id,notification);
    }

    private void callData(){

        new getAPI(ConfigAPI.API_CLIENT,token) {
            @Override
            public void doPostExecute(String JsonResult) {
                Log.e("=============","==================");
                Log.e("----------------------",JsonResult);

            }
        }.execute();
    }

    class getNoi extends AsyncTask<String ,String,Void>{
        String token;
        String response="";
        final String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date timenow ;
        public  getNoi(String token){
            this.token = token;
        }
        @Override
        protected Void doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(ConfigAPI.API_CLIENT);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                urlConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            urlConnection.setRequestProperty("content-type","application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            InputStream inputStream = null;

            // get stream
            try {
                if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    inputStream = urlConnection.getInputStream();
                } else {
                    inputStream = urlConnection.getErrorStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // parse stream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String temp="";
            while (true) {
                try {
                    if (!((temp = bufferedReader.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response += temp;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                timenow = format.parse(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int index = 1;
            DecimalFormat formatter = new DecimalFormat("###,###,###");
            final String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            try {
                JSONArray rs =  new JSONArray(response);
                for (int i = 0;i<rs.length();i++){
                    JSONObject client = new JSONObject(rs.get(i).toString());
                    if(!client.getString("status").equals("resolved")){
                        ///Xử lý khi quá số tiền
                        if(!client.getString("money_limit").equals("null")) {
                            Double money_limit = Double.parseDouble(client.getString("money_limit"));
                            Double total = Double.parseDouble(client.getString("total"));
                            if (money_limit <= total) {
                                setNotification(index, client.getString("code"), "Nợ " + currencyVN.format(Double.parseDouble(client.getString("total"))), "");
                                index++;
                            }
                        }
                            // xử lý thông báo tiền khi quá ngày
                        if(!client.getString("date_limit").equals("null")){
                            try {
                                Date date_limit = format.parse(client.getString("date_limit"));
                                if(date_limit.before(timenow)|| date_limit.equals(timenow)){
                                    setNotification(index,client.getString("code"),"Quá ngày nợ " +currencyVN.format(Double.parseDouble(client.getString("total"))),"");
                                    index++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                             //end
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
