package com.vn.quanly.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.adapter.Interface.recycleViewAction;
import com.vn.quanly.adapter.ItemNoitifications;
import com.vn.quanly.adapter.ItemPayBooks;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.Noitification;
import com.vn.quanly.model.PayBook;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentNotification extends Fragment {
    RecyclerView recyclerView;
    List<Noitification> noitificationList = new ArrayList<>();
    ItemNoitifications itemNoitifications;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        Init(view);
        ControlRecycleView();
        return view;
    }
    void Init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
    }
    void ControlRecycleView(){
        AsyntaskAPI getPay = new AsyntaskAPI(getContext(), ConfigAPI.API_CLIENT,new SaveDataSHP(getContext()).getShpToken(),false) {
            @Override
            public void setOnPreExcute() {

            }
            @SuppressLint("StaticFieldLeak")
            @Override
            public void setOnPostExcute(String JsonResult) {
                try {
                    JSONArray rs =  new JSONArray(JsonResult);
                    for (int i = 0;i<rs.length();i++){
                        JSONObject client = new JSONObject(rs.get(i).toString());
                        if(!client.getString("status").equals("resolved")){
                            Noitification noitification = new Noitification(client.getString("name"),
                                    client.getString("code"),
                                    true,client.getString("date_limit"),
                                    client.getString("money_limit"));
                            noitificationList.add(noitification);
                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    itemNoitifications = new ItemNoitifications(getContext(),recyclerView,noitificationList);
                    recyclerView.setAdapter(itemNoitifications);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        if(ToolsCheck.checkInternetConnection(getContext())){
            getPay.execute();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemNoitifications = new ItemNoitifications(getContext(),recyclerView,noitificationList);
        recyclerView.setAdapter(itemNoitifications);
        itemNoitifications.setRecycleViewAction(new recycleViewAction() {
            @Override
            public void loadMore() {

            }

        });

    }
}
