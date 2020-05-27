package com.vn.quanly.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vn.quanly.R;
import com.vn.quanly.ui.CustomerActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolsCheck extends Activity {

    public static boolean checkInternetConnection(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity == null) return false;

        if(Build.VERSION.SDK_INT >= 21){
            @SuppressLint("MissingPermission") Network[] info = connectivity.getAllNetworks();
            if(info != null){
                for (int i = 0; i < info.length; i++) {
                    if(info[i] != null && connectivity.getNetworkInfo(info[i]).isConnected())
                        return true;
                }
            }
        }else{
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if(info != null){
                for (int i = 0; i < info.length; i++) {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
            final NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnected())
                return true;
        }
        Toast.makeText(context, R.string.no_network,Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
    public void askForPermission(String permission, Integer requestCode, AppCompatActivity Activity) {
        if (ContextCompat.checkSelfPermission(Activity, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    Activity, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(Activity,
                        new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(Activity,
                        new String[]{permission}, requestCode);
            }
        }
    }
    private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    public static Boolean isNumeric(String strNum) {
        if (strNum == null || !pattern.matcher(strNum).matches()||strNum.trim().equals("")) {
            return false;
        }
        return true;
    }

    public static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }

    public static boolean isValidMobileNumber(String s)
    {
        Pattern p = Pattern.compile("^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$");

        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }
}
