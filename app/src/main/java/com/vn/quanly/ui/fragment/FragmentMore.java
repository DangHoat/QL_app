package com.vn.quanly.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.vn.quanly.R;
import com.vn.quanly.adapter.Interface.connectMainActivity;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.ui.MainActivity;
import com.vn.quanly.ui.StartActivity;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentMore extends Fragment {
    LinearLayout controlUser;
    Button logout;
    Button addNewUsers;
    Button setting;
    Button introduce;
    TextView name;
    TextView user_email;
    TextView tvEmail;
    ImageButton btncontrol;
    Button forgotPass;
    connectMainActivity connectMainActivity;
    LinearLayout linearLayoutUser;
    SaveDataSHP saveDataSHP;
    LinearLayout resgiter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        Init(view);
        ButtonClick(addNewUsers);
        ButtonClick(setting);
        ButtonClick(introduce);
        ButtonClick(forgotPass);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataSHP.removeSHP();
                getActivity().startActivity(new Intent(getContext(), StartActivity.class));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getActivity().finishAffinity();
                }
                getActivity().finish();
            }
        });
        btncontrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectMainActivity.ControlUser();
            }
        });
        return view;
    }
    void Init(View view){
        saveDataSHP = new SaveDataSHP(getContext());
        logout = view.findViewById(R.id.logout);
        controlUser = view.findViewById(R.id.controlUser);
        addNewUsers = view.findViewById(R.id.addNewUsers);
        setting = view.findViewById(R.id.setting);
        introduce = view.findViewById(R.id.introduce);
        linearLayoutUser = view.findViewById(R.id.linearLayoutUser);
        resgiter = view.findViewById(R.id.resgiter);
        name = view.findViewById(R.id.name);
        tvEmail = view.findViewById(R.id.tvEmail);
        user_email = view.findViewById(R.id.user_email);
        btncontrol = view.findViewById(R.id.btncontrol);
        forgotPass = view.findViewById(R.id.forgotPass);
        if(!saveDataSHP.getString(SaveDataSHP.SHP_PROMISE).equals("1")){
            resgiter.setVisibility(View.GONE);
            linearLayoutUser.setVisibility(View.GONE);
        }

        try {
            name.setText(saveDataSHP.getInfo().get(SaveDataSHP.SHP_NAME).toString());
            tvEmail.setText(saveDataSHP.getInfo().get(SaveDataSHP.SHP_EMAI).toString());
            user_email.setText(saveDataSHP.getInfo().get(SaveDataSHP.SHP_EMAI).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public com.vn.quanly.adapter.Interface.connectMainActivity getConnectMainActivity() {
        return connectMainActivity;
    }
    void ButtonClick(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.addNewUsers:
                        //Toast.makeText(getContext(),"addNewUsers",Toast.LENGTH_SHORT).show();
                        connectMainActivity.Resgiter();
                        return;
                    case R.id.setting:
                        //Toast.makeText(getContext(),"setting",Toast.LENGTH_SHORT).show();
                        connectMainActivity.Setting();
                        return;
                    case R.id.introduce:
                        //Toast.makeText(getContext(),"introduce",Toast.LENGTH_SHORT).show();
                        connectMainActivity.Introduce();
                        return;
                    case R.id.forgotPass:
                        displayAlertDialog();
                        return;
                    default:return;
                }
            }
        });
    }

    public void displayAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.change_password, null);

        final EditText oldPass = (EditText) alertLayout.findViewById(R.id.oldPass);
        final EditText newPass = (EditText) alertLayout.findViewById(R.id.newPass);
        final EditText reNewPass = (EditText) alertLayout.findViewById(R.id.reNewPass);
        final CheckBox cbShowPassword = (CheckBox) alertLayout.findViewById(R.id.cb_ShowPassword);

        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    oldPass.setTransformationMethod(null);
                    newPass.setTransformationMethod(null);
                    reNewPass.setTransformationMethod(null);
                }else{
                    oldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    reNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Thay đổi mật khẩu");


        alert.setNegativeButton("Bỏ qua", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String olPass = oldPass.getText().toString().trim();
                final String newPassword = newPass.getText().toString().trim();
                final String rePass = reNewPass.getText().toString().trim();
                if(olPass.equals("")){
                    oldPass.setError("Nhập mật khẩu để xác nhận");
                    oldPass.requestFocus();
                    return;
                }
                if(!newPassword.equals(rePass)||newPassword.equals("")){
                    reNewPass.setError("Mật khẩu nhập lại không khớp hoặc đang trống");
                    reNewPass.requestFocus();
                    return;
                }

                JSONObject data = new JSONObject();
                try {
                    data.put("password",olPass);
                    data.put("password_confirm",rePass);
                    data.put("new_password",newPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AsyntaskAPI changepass = new AsyntaskAPI(getContext(),data, ConfigAPI.API_CHANGE_PASS,"PATCH",new SaveDataSHP(getContext()).getShpToken()) {
                    @Override
                    public void setOnPreExcute() {

                    }
                    @Override
                    public void setOnPostExcute(String JsonResult) {
                        try {
                            JSONObject rs = new JSONObject(JsonResult);
                            Log.e("JsonResult",JsonResult);
                            if(!rs.toString().equals("")&& rs.getString("message").equals("Successfully")){
                                dialog.dismiss();
                                SaveDataSHP saveDataSHP = new SaveDataSHP(getContext());
                                saveDataSHP.SaveToken(rs.getString("token"));
                                saveDataSHP.setData(SaveDataSHP.SHP_PASS,newPassword);
                                Toast.makeText(getContext(),"Bạn thay đổi thành công mật khẩu",Toast.LENGTH_SHORT).show();

                            }
                            if(!rs.toString().equals("")&& rs.getString("message").equals("Fails")){
                                oldPass.setError("Mật khẩu bạn nhập không đúng");
                                oldPass.requestFocus();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                if(ToolsCheck.checkInternetConnection(getContext())){
                    changepass.execute();
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.connectMainActivity = (connectMainActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectMainActivity =null;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }
}
