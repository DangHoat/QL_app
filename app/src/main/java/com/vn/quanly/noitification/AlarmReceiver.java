package com.vn.quanly.noitification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vn.quanly.ui.MainActivity;
import com.vn.quanly.utils.SaveDataSHP;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class AlarmReceiver extends BroadcastReceiver {
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        String token = new SaveDataSHP(context).getShpToken();
        Intent newIntent = new Intent(context,Service.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("token", token);
        newIntent.putExtras(mBundle);
        context.startService(newIntent);

    }
}
