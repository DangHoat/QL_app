package com.vn.quanly.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentNotification extends Fragment {
    RecyclerView recyclerView;
    List<Noitification> noitificationList = new ArrayList<>();
    ItemNoitifications itemNoitifications;
    final String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date timenow ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        Init(view);
        try {
            timenow = format.parse(today);
            Log.e("timenow",today);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ControlRecycleView();
        return view;
    }
    void Init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
    }
    void ControlRecycleView(){
        AsyntaskAPI getPay = new AsyntaskAPI(getContext(), ConfigAPI.API_CLIENT,new SaveDataSHP(getContext()).getShpToken(),true) {
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
                                if(!client.getString("date_limit").equals("null")){
                                    Date date_limit = format.parse(client.getString("date_limit"));
                                    if(date_limit.before(timenow)||date_limit.equals(timenow)){
                                        Noitification noitification = new Noitification(
                                                client,
                                                client.getString(
                                                        "name"),
                                                client.getString("code"),
                                                true,client.getString("date_limit"),
                                                client.getString("total"));
                                        noitificationList.add(noitification);
                                    }
                                }
                            }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    itemNoitifications = new ItemNoitifications(getContext(),recyclerView,noitificationList);
                    recyclerView.setAdapter(itemNoitifications);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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
