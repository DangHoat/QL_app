package com.vn.quanly.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vn.quanly.R;
import com.vn.quanly.ViewModel.MainViewModel;
import com.vn.quanly.adapter.Interface.connectMainActivity;
import com.vn.quanly.adapter.Interface.resetClient;
import com.vn.quanly.noitification.AlarmReceiver;
import com.vn.quanly.ui.fragment.FragmentAddBill;
import com.vn.quanly.ui.fragment.FragmentClient;
import com.vn.quanly.ui.fragment.FragmentMore;
import com.vn.quanly.ui.fragment.FragmentNotification;
import com.vn.quanly.ui.fragment.FragmentPaybook;
import com.vn.quanly.ui.fragment.FragmentResgiter;
import com.vn.quanly.ui.fragment.FragmentSetting;
import com.vn.quanly.ui.fragment.FramentControlAccount;
import com.vn.quanly.utils.SaveDataSHP;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements connectMainActivity,FragmentSetting.reCallAlarm {
    BottomNavigationView bottomNavigationView;
    LinearLayout content_layout;
    Boolean isHome ;
    Boolean isSetFromMore = false;
    Boolean isSearch;
    ActionBar actionBar;
    Context context;
    Integer indexPage;
    AlarmReceiver alarmReceiver;
    PendingIntent pendingIntent;
    private static FragmentPaybook fragmentPaybook ;
    private static FragmentMore fragmentMore ;
    AlarmManager alarmManager;
    private static  MainActivity main;
    MainViewModel mainViewModel;
    private resetClient reset;

    @Override
    protected void onStart() {
        super.onStart();
        indexPage = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getNumberPage().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
            switch (integer){
                case 0:
                    actionBar.setTitle("Sổ Giao Dịch");
                    setFragment(fragmentPaybook);
                    return;
                case 1:
                    actionBar.setTitle("Thêm Hóa Đơn");
                    FragmentAddBill fragmentAddData =new FragmentAddBill();
                    setFragment(fragmentAddData);
                    isHome = false;
                    return;
                case 2:
                    actionBar.setTitle("Thông Báo Hôm Nay");
                    FragmentNotification fragmentNotification = new FragmentNotification();
                    setFragment(fragmentNotification);
                    isHome = false;
                    return;
                case 3:
                    setFragment(fragmentMore);
                    actionBar.setTitle("Mở Rộng");
                    isHome = false;
                    return;

            }
            }
        });
        Init();
        actionBar.setTitle("Sổ Giao Dịch");
        fragmentMore = new FragmentMore();
        fragmentPaybook = new FragmentPaybook();
        this.setResetClient(fragmentPaybook);
        isHome = true;
        isSearch = false;
        ControlBottomNav();
        if(!new SaveDataSHP(getApplicationContext()).getString(SaveDataSHP.SHP_PROMISE).equals("1")){
            bottomNavigationView.getMenu().removeItem(R.id.nhapxuat);
        }
        setFragment(fragmentPaybook);
        //SET BÁO THỨC THÔNG BÁO//
        setAlarm();
    }
    private void Init() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        content_layout = findViewById(R.id.content_layout);
        alarmReceiver = new AlarmReceiver();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        context = this;
        actionBar = getSupportActionBar();
        if(actionBar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

    }
    private void ControlBottomNav(){
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(isOpen){
                    bottomNavigationView.setVisibility(View.GONE);
                }else {
                    if(isSetFromMore){
                        bottomNavigationView.setVisibility(View.GONE);
                    }else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.sogiadich:
                        indexPage = 0;
                        mainViewModel.setNumberPage(indexPage);
                        return true;
                    case R.id.nhapxuat:
                        indexPage = 1;
                        mainViewModel.setNumberPage(indexPage);
                        return true;
                    case R.id.thongbao:
                        indexPage = 2;
                        mainViewModel.setNumberPage(indexPage);
                        return true;
                    case R.id.them:
                        indexPage = 3;
                        mainViewModel.setNumberPage(indexPage);
                        return true;
                }
                return false;
            }
        });
    }

    public void setResetClient(com.vn.quanly.adapter.Interface.resetClient resetClient) {
        this.reset = resetClient;
    }

    public void setFragment(Fragment f){
        bottomNavigationView.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_layout,f);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(isSetFromMore){
            isSetFromMore= false;
            setFragment(fragmentMore);
            actionBar.setTitle("Mở Rộng");
            if(!new SaveDataSHP(getApplicationContext()).getString(SaveDataSHP.SHP_PROMISE).equals("1")){
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
            }else {
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
            }

            return;
        }
        if(isHome){
            super.onBackPressed();
        }else {
            isHome = true;
            setFragment(fragmentPaybook);
            actionBar.setTitle("Sổ Giao Dịch");
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        }
    }
    @Override
    public void Resgiter() {
        FragmentResgiter fragmentResgiter =new FragmentResgiter();
        setFragment(fragmentResgiter);
        actionBar.setTitle("Thêm Nhân Viên");
        bottomNavigationView.setVisibility(View.GONE);
        isSetFromMore = true;
    }
    @Override
    public void Setting() {
        FragmentSetting fragmentSetting = new FragmentSetting();
        setFragment(fragmentSetting);
        actionBar.setTitle("Cài đặt");
        bottomNavigationView.setVisibility(View.GONE);
        isSetFromMore = true;
    }

    @Override
    public void Introduce() {
        FragmentClient fragmentIntroduce = new FragmentClient();
        setFragment(fragmentIntroduce);
        actionBar.setTitle("Danh sách khách hàng");
        bottomNavigationView.setVisibility(View.GONE);
        isSetFromMore = true;
    }

    @Override
    public void ControlUser() {
        FramentControlAccount framentControlAccount = new FramentControlAccount();
        setFragment(framentControlAccount);
        actionBar.setTitle("Quản lý Nhân Viên");
        bottomNavigationView.setVisibility(View.GONE);
        isSetFromMore = true;
    }

    public void setAlarm(){
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        int [] time = new SaveDataSHP(getApplicationContext()).getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, time[0])   ;
        calendar.set(Calendar.MINUTE, time[1]);
        int timeRecall = 8*60*60*1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),timeRecall,pendingIntent);
    }

    @Override
    public void reCallInMain() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        int [] time = new SaveDataSHP(getApplicationContext()).getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, time[0])   ;
        calendar.set(Calendar.MINUTE, time[1]);
        int timeRecall = 8*60*60*1000;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),timeRecall,pendingIntent);
            setAlarm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MainActivity", "onStop");
    }


    @Override
    protected void onResume() {
        super.onResume();
        reset.resetClient();

    }
}
