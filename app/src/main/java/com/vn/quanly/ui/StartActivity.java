package com.vn.quanly.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.vn.quanly.R;
import com.vn.quanly.SQLlite.Database;
import com.vn.quanly.noitification.Service;
import com.vn.quanly.ui.fragment.FragmentForgot;
import com.vn.quanly.ui.fragment.FragmentLogin;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;

public class StartActivity extends AppCompatActivity  implements FragmentForgot.backToLogin,FragmentLogin.setForgot {
    LinearLayout linearLayout;
    FragmentLogin loginFragment;
    boolean isForgot;
    Database database;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        loginFragment = new FragmentLogin() ;
        ActionBar actionBar = getSupportActionBar();
        database = new Database(getApplicationContext());
        if(actionBar != null){
            actionBar.hide();
        }
        linearLayout = findViewById(R.id.content_layout);
        SaveDataSHP saveDataSHP = new SaveDataSHP(getApplication());
        if(saveDataSHP.SHP_Check()){
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }else{
            setFragment(loginFragment);
        }
    }
    public void setFragment(Fragment f){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_layout,f);
        fragmentTransaction.commit();
    }

    @Override
    public void backToLogin() {
        setFragment(loginFragment);
    }

    @Override
    public void onBackPressed() {
        if(isForgot){
            isForgot = false;
            setFragment(loginFragment);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void setForgot() {
        isForgot = true;
        FragmentForgot fragmentForgot = new FragmentForgot();
        setFragment(fragmentForgot);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
}
