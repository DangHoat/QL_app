package com.vn.quanly.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vn.quanly.R;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;
import com.vn.quanly.utils.VNCharacterUtils;

import org.apache.poi.ss.formula.functions.T;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentResgiter extends Fragment {
    EditText edEmail;
    EditText edLastname;
    EditText edFirstname;
    EditText edPass;
    EditText edRePass;
    EditText telecom;
    RadioButton nv,ql;
    RadioGroup role;
    Button register;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resgister,container,false);
        Init(view);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton radioButton = getView().findViewById(role.getCheckedRadioButtonId());
                final String email = edEmail.getText().toString().trim();
                String name = edFirstname.getText().toString().trim()+" "+edFirstname.getText().toString().trim();
                String password = edPass.getText().toString().trim();
                String repassword = edRePass.getText().toString().trim();
                String tel = telecom.getText().toString().trim();
                String roles = "1";
                if(VNCharacterUtils.removeAccent(radioButton.getText().toString().trim().toLowerCase()).equals("nhan vien")){
                     roles = "2";
                }else {
                     roles = "1";
                }
                if(email.equals("")){
                    edEmail.setError("Không được để trống");
                    edEmail.requestFocus();
                    return;
                }
                if(!ToolsCheck.isValidEmail(email)){
                    edEmail.setError("Email đúng phải đúng định dạng!");
                    edEmail.requestFocus();
                    return;
                }
                if(!password.equals(repassword)){
                    edRePass.setError("Không khớp mật khẩu");
                    edEmail.requestFocus();
                    return;
                }
                if(password.length()<6){
                    edRePass.setError("Mật khẩu ít nhất 6 kí tự");
                    edEmail.requestFocus();
                    return;
                }
                JSONObject data = new JSONObject();
                try {
                    data.put("name",name);
                    data.put("email",email);
                    data.put("role",roles);
                    data.put("password",password);
                    data.put("telecom",tel);
                    data.put("password_confirm",repassword);
                    data.put("permissions",roles);

                    final AsyntaskAPI res = new AsyntaskAPI(getContext(),data, ConfigAPI.API_RESGITER,"PUT",new SaveDataSHP(getContext()).getShpToken()) {
                        @Override
                        public void setOnPreExcute() {

                        }

                        @Override
                        public void setOnPostExcute(String JsonResult) {
                            try {
                                JSONObject rs = new JSONObject(JsonResult);
                                Log.e("JSONObject",rs.toString());
                                if(!rs.toString().equals("")){
                                    if(rs.getString("message").equals("successfully")){
                                        Toast.makeText(getContext(),"Bạn đã tạo tài khoản thành công",Toast.LENGTH_SHORT).show();
                                        edEmail.setText("");
                                        edLastname.setText("");
                                        edFirstname.setText("");
                                        telecom.setText("");
                                        edPass.setText("");
                                        edRePass.setText("");

                                    }else {
                                        edEmail.setError("Hãy kiểm tra lại email!");
                                        edEmail.requestFocus();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    if(ToolsCheck.checkInternetConnection(getContext())){
                    res.execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void Init(View view) {
        edEmail = view.findViewById(R.id.edEmail);
        edLastname = view.findViewById(R.id.lastName);
        edFirstname = view.findViewById(R.id.firstName);
        telecom = view.findViewById(R.id.telecom);
        edPass = view.findViewById(R.id.newPass);
        edRePass = view.findViewById(R.id.repassword);
        nv = view.findViewById(R.id.nv);
        ql = view.findViewById(R.id.ql);
        role = view.findViewById(R.id.role);

        register = view.findViewById(R.id.register);
    }
}
