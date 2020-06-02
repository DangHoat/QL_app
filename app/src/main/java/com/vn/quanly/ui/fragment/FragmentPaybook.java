package com.vn.quanly.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;

import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.SQLlite.Database;
import com.vn.quanly.adapter.Interface.resetClient;
import com.vn.quanly.adapter.ItemPayBooks;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.Client;
import com.vn.quanly.model.PayBook;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;
import com.vn.quanly.utils.VNCharacterUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FragmentPaybook extends Fragment implements resetClient {
    private List<PayBook> payBooks = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemPayBooks itemPayBooks;
    private TextView total;
    CardView cardView;
    SearchView searchView;
    Database database;
    Double totalCost = 0.0;
    DatePickerDialog.OnDateSetListener dateSetListener;
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paybook,container,false);
        database = new Database(getContext());
        Init(view);
        ControlListPayBooks();
        return view;
    }
    private void Init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        total =  view.findViewById(R.id.total);
        cardView =  view.findViewById(R.id.cardView);
        cardView.setVisibility(View.GONE);
    }
    private void ControlListPayBooks(){
            AsyntaskAPI getPay = new AsyntaskAPI(getContext(), ConfigAPI.API_CLIENT,new SaveDataSHP(getContext()).getShpToken(),true) {
            @Override
            public void setOnPreExcute() {
                totalCost= 0.0;
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("StaticFieldLeak")
            @Override
            public void setOnPostExcute(String JsonResult) {
                database.clearAll();

                try {
                    JSONArray rs =  new JSONArray(JsonResult);
                    for (int i = 0;i<rs.length();i++){
                        JSONObject client = new JSONObject(rs.get(i).toString());
                        totalCost +=  Double.parseDouble(client.getString("total"));
                        Client itemClient =  new Client(
                                client.getString("code"),
                                client.getString("name"),
                                client.getString("address"),
                                client.getString("telephone"),
                                client.getString("total"));
                        database.AddNote(getContext(),itemClient);

                        PayBook payBook =  new PayBook(
                                client.getString("name"),
                                client.getString("code"),
                                client.getString("status"),
                                client.getString("money_limit"),
                                client.getString("date_limit"),
                                client.getString("address"),
                                client.getString("telephone"),
                                client.getString("total"),
                                client.getString("status").equals("pending"));
                        payBooks.add(payBook);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    itemPayBooks = new ItemPayBooks(getContext(),recyclerView,payBooks,5);
                    recyclerView.setAdapter(itemPayBooks);
                    cardView.setVisibility(View.VISIBLE);
                    total.setText(currencyVN.format(totalCost));
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            };
        if(ToolsCheck.checkInternetConnection(getContext())){
            getPay.execute();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        payBooks.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_view, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        searchView = (SearchView) item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if((payBooks!=null&&payBooks.size()!=0)){
                    itemPayBooks.getFilter().filter(newText +"");
                }
                return false;
            }
        });
       return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_client:
                CreateClient();
                return true;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void CreateClient(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.options_menu, null);
        final EditText edTenkhachhang = alertLayout.findViewById(R.id.edDiachiCT);
        final EditText edDiachi = alertLayout.findViewById(R.id.edLoạiHang);
        final EditText edSodienthoai = alertLayout.findViewById(R.id.edTenHang);
        final EditText edTratruoc = alertLayout.findViewById(R.id.edNote);
        final EditText edHanmuc = alertLayout.findViewById(R.id.edDonvi);
        final TextView limTime =alertLayout.findViewById(R.id.edLimTime);
        final EditText edCode = alertLayout.findViewById(R.id.edCode);
        final TextView tvCode =alertLayout.findViewById(R.id.tvCode);
        edCode.setVisibility(View.VISIBLE);
        tvCode.setVisibility(View.VISIBLE);
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year+"-"+month+"-"+dayOfMonth;
                limTime.setText(date);
                limTime.setTextColor(Color.BLACK);
            }
        };

        limTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Thêm khách hàng");
        alert.setView(alertLayout);
        alert.setCancelable(false);

        alert.setNegativeButton("Bỏ Qua", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setPositiveButton("Thêm khách hàng ", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String tenkhachhang = edTenkhachhang.getText().toString().trim();
                final String makhachhang = edCode.getText().toString().trim();
                final String diachi = edDiachi.getText().toString().trim();
                final String sodienthoai = edSodienthoai.getText().toString().trim();
                String hanmuc = edHanmuc.getText().toString().trim();
                if(!hanmuc.equals("")){
                  hanmuc = hanmuc.replace(",","");
                }
                String tratruoc = edTratruoc.getText().toString().trim();
                final String limitTime = limTime.getText().toString().trim().equals("")?"":limTime.getText().toString().trim();
                JSONObject data = new JSONObject();
                try {
                    data.put("code",VNCharacterUtils.replaceWhiteSpace(makhachhang));
                    data.put("name",tenkhachhang);
                    data.put("address",diachi);
                    data.put("status","resolved");
                    data.put("telephone",sodienthoai);
                    data.put("money_limit",hanmuc);
                    data.put("date_limit",limitTime);
                    data.put("note","");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                @SuppressLint("StaticFieldLeak") AsyntaskAPI creatClient = new AsyntaskAPI(getContext()
                        ,data,ConfigAPI.API_CLIENT,"POST",new SaveDataSHP(getContext()).getShpToken()) {
                    @Override
                    public void setOnPreExcute() {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void setOnPostExcute(String JsonResult) {
                        //pay_enough
                        try {
                            JSONObject rs = new JSONObject(JsonResult);
                            if (!rs.toString().equals("") && rs.getString("message").equals("Successfully")){
                                Toast.makeText(getContext(), "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show();
                                Client client =  new Client(makhachhang,tenkhachhang,diachi,sodienthoai,"0");
                                database.AddNote(getContext(),client);
                            }else {
                                Toast.makeText(getContext(), "Hãy kiểm tra lại", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                if(ToolsCheck.checkInternetConnection(getContext())){
                    creatClient.execute();
                }

                dialog.dismiss();

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
//Cập nhập lại tiền của khách đã thay đổi

    @Override
    public void resetClient() {
        Log.e("resetClient","resetClient");
        if(itemPayBooks!= null && !payBooks.isEmpty()){
            SaveDataSHP saveDataSHP  = new SaveDataSHP(getContext());
            String codelient = saveDataSHP.getString("code_client");
            for (PayBook v:payBooks){
                if(v.getCodeClient().equals(codelient)){
                    v.setTotal(saveDataSHP.getString("total_client"));
                    break;
                }
            }
            itemPayBooks.notifyDataSetChanged();
        }
    }
}
