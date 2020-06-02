package com.vn.quanly.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.SQLlite.Database;
import com.vn.quanly.adapter.Interface.deleteClient;
import com.vn.quanly.adapter.ListClient;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.Client;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;
import com.vn.quanly.utils.VNCharacterUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentClient extends Fragment {
    SearchView searchView;
    RecyclerView recyclerView;
    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
    private List<Client> allList = new ArrayList<>();
    private List<Client> tempList = new ArrayList<>();
    private ListClient listClient;
    private Database database;
    ProgressDialog progressDialog = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client,container,false);
        Init(view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listClient);
        listClient.setDeleteClient(new deleteClient() {
            @Override
            public void delete(final Client codeClient) {
                JSONObject data = null;
                try {
                    data = new JSONObject();
                    data.put("code",codeClient.getCode());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ToolsCheck.checkInternetConnection(getContext())){
                    AsyntaskAPI callClientByCode =   new AsyntaskAPI(getActivity(),data, ConfigAPI.API_CLIENT_BY_CODE,"POST",new SaveDataSHP(getContext()).getShpToken()) {
                        @Override
                        public void setOnPreExcute() {
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Vui lòng chờ...");
                            progressDialog.show();
                        }

                        @Override
                        public void setOnPostExcute(String JsonResult) {
                            progressDialog.cancel();
                            try {
                                JSONObject rs = new JSONObject(JsonResult);
                                Log.e("JSONObject",rs.toString());
                                if(!rs.toString().equals("")){
                                    if(rs.getString("message").toLowerCase().equals("successfully")){
                                        ShowDialogDelete(codeClient,rs.getString("total"));
                                    }
                                    if(rs.getString("message").equals("Undefined")){
                                        Toast.makeText(getContext(),"Khách đã không tồn tại!",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(getContext(),"Vui lòng kiểm tra lại!",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    callClientByCode.setShowLoading(false);
                    callClientByCode.execute();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equals("")){

                    tempList.clear();
                    for (Client client : database.getAllNode()){
                        if(client.getCode().toLowerCase().indexOf(newText.toLowerCase())>=0 || VNCharacterUtils.removeAccent(client.getName()).toLowerCase().indexOf(newText.toLowerCase())>0){
                            tempList.add(client);
                        }
                    }

                }else {
                    //allList.addAll(tempList);
                    tempList.clear();
                    tempList.addAll(database.getAllNode());

                }
                listClient.notifyDataSetChanged();
                return false;
            }
        });
        return view;
    }

    void Init(View view) {
        searchView = view.findViewById(R.id.search);
        recyclerView = view.findViewById(R.id.clientRcv);
        database = new Database(getContext());
        tempList = database.getAllNode();
        listClient = new ListClient(getContext(),tempList);
    }
    void ShowDialogDelete(final Client data, String total){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Xóa khách hàng");
        alert.setMessage("Khách hàng " +"\"" +data.getName()+"\"" + " nợ " +format.format(Double.parseDouble(total)));
        alert.setCancelable(false);
        alert.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(ToolsCheck.checkInternetConnection(getContext())){
                    try {
                        new AsyntaskAPI(getActivity(), new JSONObject().put("code", data.getCode()), ConfigAPI.API_CLIENT, "DELETE", new SaveDataSHP(getContext()).getShpToken()) {
                            @Override
                            public void setOnPreExcute() {

                            }

                            @Override
                            public void setOnPostExcute(String JsonResult) {
                                try {
                                    JSONObject rs = new JSONObject(JsonResult);
                                    Log.e("JSONObject",rs.toString());
                                    if(!rs.toString().equals("")){
                                        if(rs.getString("message").toLowerCase().equals("successfully")){
                                            Toast.makeText(getContext(),"Xóa thành công!",Toast.LENGTH_SHORT).show();
                                            resetList(data);
                                        }
                                        if(rs.getString("message").equals("Undefined")){
                                            Toast.makeText(getContext(),"Khách đã không tồn tại!",Toast.LENGTH_SHORT).show();
                                            resetList(data);
                                        }
                                        if(rs.getString("message").equals("Server Error")){
                                            Toast.makeText(getContext(),"Khách đã không tồn tại!",Toast.LENGTH_SHORT).show();
                                            resetList(data);
                                        }
                                    }else {
                                        Toast.makeText(getContext(),"Vui lòng kiểm tra lại!",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    void resetList(Client client){
        try {
            database.DeleteNote(client.getCode());
            tempList.remove(client);
            listClient.notifyDataSetChanged();
        }catch (Exception e){
            Log.e("err",e.toString());
        }


    }

}
