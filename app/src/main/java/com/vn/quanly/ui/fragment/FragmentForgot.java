package com.vn.quanly.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vn.quanly.R;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentForgot extends Fragment {
    backToLogin backToLogin;
    Button btnbacktologin;
    EditText edEmail;
    Button btnAccept;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Quên Mật Khẩu");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        Init(view);
        btnbacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToLogin.backToLogin();
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edEmail.getText().toString().trim();
                if(!ToolsCheck.isValidEmail(email)){
                    edEmail.setError("Email không hợp lệ");
                    edEmail.requestFocus();
                    return;
                }
                JSONObject data = new JSONObject();
                try {
                    data.put("email_forgot", email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(ToolsCheck.checkInternetConnection(getContext())){
                    new AsyntaskAPI(getContext(), data, ConfigAPI.API_FORGOT, "POST", new SaveDataSHP(getContext()).getShpToken()) {
                        @Override
                        public void setOnPreExcute() {

                        }

                        @Override
                        public void setOnPostExcute(String JsonResult) {
                            Log.e("Forgot",JsonResult);
                            Toast.makeText(getContext(),"Vui lòng kiểm tra email để reset mật khẩu",Toast.LENGTH_SHORT).show();
                            edEmail.setText("");
                            backToLogin.backToLogin();

                        }
                    }.execute();
                }
            }
        });
        return view;
    }

    private void Init(View view) {
        btnbacktologin = view.findViewById(R.id.backtologin);
        edEmail = view.findViewById(R.id.edEmail);
        btnAccept = view.findViewById(R.id.btnAccept);
    }

    public interface backToLogin{
        void backToLogin();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        backToLogin = (backToLogin) context;
    }
}
