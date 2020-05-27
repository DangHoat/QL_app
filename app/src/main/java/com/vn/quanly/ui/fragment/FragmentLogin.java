package com.vn.quanly.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vn.quanly.R;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.ui.MainActivity;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentLogin extends Fragment {
    EditText email;
    EditText password;
    Button login;
    Button forgotPass;
    setForgot setforgot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        Init(view);
        Login();
        Forgot();
        return view;
    }
    private void Init(View view){
        email = view.findViewById(R.id.lastName);
        password = view.findViewById(R.id.newPass);
        login = view.findViewById(R.id.login);
        forgotPass = view.findViewById(R.id.forgotPass);
    }
    private void Login(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String text_email = email.getText().toString().trim();
                final String text_pass  = password.getText().toString().trim();
                if(!new ToolsCheck().isValidEmail(text_email)){
                    email.setError("Định dạng email không đúng");
                    email.requestFocus();
                    return;
                }
                if(text_pass.equals("")){
                    password.setError("Password không được để trống");
                    password.requestFocus();
                    return;
                }
                JSONObject data= new JSONObject();
                try {
                    data.put("email",text_email);
                    data.put("password",text_pass);
                    if(ToolsCheck.checkInternetConnection(getContext())) {
                        new AsyntaskAPI(getContext(), data, ConfigAPI.API_LOGIN, "POST") {
                            @Override
                            public void setOnPreExcute() {

                            }

                            @Override
                            public void setOnPostExcute(String JsonResult) {
                                try {
                                    JSONObject rs = new JSONObject(JsonResult);
                                    if (!rs.toString().equals("")) {
                                        if (rs.get("message").toString().equals("Successfully")) {
                                            SaveDataSHP saveDataSHP = new SaveDataSHP(getContext());
                                            saveDataSHP.SaveToken(rs.get("token").toString());
                                            saveDataSHP.SaveAuth(text_email, text_pass);
                                            saveDataSHP.setInfo(rs.getJSONObject("account"));
                                            saveDataSHP.setSHP(SaveDataSHP.SHP_PROMISE, Integer.toString(rs.getInt("role")));
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                getActivity().finishAffinity();
                                            }
                                            getActivity().finish();
                                            Toast.makeText(getContext(), "Đăng nhập hệ thống thành công!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Vui lòng kiểm tra lại tài khoản!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("error", e.toString());
                                }
                            }
                        }.execute();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private  void Forgot(){
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setforgot.setForgot();
            }
        });
    }
    public interface setForgot{
        void setForgot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setforgot =(setForgot)context;
    }
}
