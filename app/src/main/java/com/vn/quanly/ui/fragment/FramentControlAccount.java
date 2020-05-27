package com.vn.quanly.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.adapter.ItemPayBooks;
import com.vn.quanly.adapter.UserSystem;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.PayBook;
import com.vn.quanly.model.User;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FramentControlAccount extends Fragment {
    RecyclerView recyclerView;
    ArrayList<User> list = new ArrayList<>();
    UserSystem userSystem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_recycleview,container,false);
        Init(view);

        AsyntaskAPI getPay = new AsyntaskAPI(getContext(), ConfigAPI.API_ALL_USER,new SaveDataSHP(getContext()).getShpToken(),false) {
            @Override
            public void setOnPreExcute() {

            }
            @SuppressLint("StaticFieldLeak")
            @Override
            public void setOnPostExcute(String JsonResult) {
                try {
                    JSONArray rs =  new JSONArray(JsonResult);
                    for (int i = 0;i<rs.length();i++){

                        JSONObject newUser = new JSONObject(rs.get(i).toString());

                        String permissions = newUser.getString("permissions");
                        boolean role = false;
                        if(permissions.toString().equals("null")|| permissions == null){
                            role = false;
                            continue;
                        }
                        else {
                           JSONObject role_id = new JSONObject(permissions);
                           if(Integer.toString(role_id.getInt("role_id")).equals("1")){
                               role = true;
                           }
                        }

                        User user = new User(newUser.getString("name"),newUser.getString("email"),role);
                        list.add(user);
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    userSystem = new UserSystem(getContext(),list);
                    recyclerView.setAdapter(userSystem);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("err",e.toString());
                }

            }
        };
        if(ToolsCheck.checkInternetConnection(getContext())){
            getPay.execute();
        }
        return view;
    }
     void Init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
     }
}
