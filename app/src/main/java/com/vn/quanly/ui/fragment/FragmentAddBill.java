package com.vn.quanly.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.vn.quanly.R;
import com.vn.quanly.SQLlite.Database;
import com.vn.quanly.adapter.Interface.clickItemSearch;
import com.vn.quanly.adapter.ItemBills;
import com.vn.quanly.adapter.SearchClient;
import com.vn.quanly.adapter.SwipeToDeleteCallback;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.BillOfSale;
import com.vn.quanly.model.Client;
import com.vn.quanly.ui.CurrencyEditText;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;
import com.vn.quanly.utils.VNCharacterUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentAddBill extends Fragment {
    private EditText editTextID;
    private EditText editTextName;
    private EditText editAddress;
    private EditText editTelecom;
    private TextView totalCost;
    private PopupMenu popup;
    private RecyclerView rvItem;
    private RecyclerView recyclerViewID;
    ConstraintLayout constraintlayout;
    private Button btnAccept;
    private Button btnAddBill;
    private final ArrayList<BillOfSale> billOfSales = new ArrayList<>();
    private ItemBills itemBill;
    private SearchClient searchView;
    private String lastAddress = "";
    private ArrayList<BillOfSale> bills = new ArrayList<>();
    String token;
    Context context;
    DatePickerDialog.OnDateSetListener dateSetListener;
    private static final int MENU = Menu.FIRST;
    ArrayList<String> list_loaihang = new ArrayList<>();
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
    DecimalFormat df = new DecimalFormat("0.00");
    Database database;

    private List<Client> clientList = new ArrayList<>();
    private List<Client> listResult = new ArrayList<>();



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_bill,container,false);
        context = getContext();
        token =new SaveDataSHP(context).getShpToken();
        database = new Database(getContext());
        clientList = database.getAllNode();

        Init(view);
        AcceptControl();
        RecycleViewControl();
        SwipeToDeleteAndUndo();

        recyclerViewID.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = new SearchClient(getContext(),listResult);
        recyclerViewID.setAdapter(searchView);
        searchView.setClickItemSearch(new clickItemSearch() {
            @Override
            public void onClick(Client client) {
                editAddress.setText(client.getAddress());
                editTextID.setText(client.getCode());
                editTelecom.setText(client.getTelecom());
                editTextName.setText(client.getName());
                listResult.clear();
                searchView.notifyDataSetChanged();
            }
        });
        editTextID.addTextChangedListener(onTextChangedListener());
        KeyboardEvent();
        return view;
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                listResult.clear();
                searchView.notifyDataSetChanged();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged",s.toString());
                if(!s.toString().trim().equals("")){
                    for (Client t : clientList){
                        if(t.getCode().toLowerCase().indexOf(s.toString().toLowerCase())>=0){
                            listResult.add(t);
                        }
                    }
                }
                else {
                    editAddress.setText("");
                    //editTextID.setText("");
                    editTelecom.setText("");
                    editTextName.setText("");
                }
                searchView.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {


            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void Init(View view){
        editTextID = view.findViewById(R.id.editTextID);
        editTextName = view.findViewById(R.id.editTextName);
        editAddress = view.findViewById(R.id.editAddress);
        editTelecom = view.findViewById(R.id.editTelecom);
        totalCost = view.findViewById(R.id.totalCost);
        rvItem = view.findViewById(R.id.rvItem);
        btnAccept = view.findViewById(R.id.btnAccept);
        recyclerViewID = view.findViewById(R.id.recyclerViewID);
        btnAddBill = view.findViewById(R.id.btnAddBill);
        constraintlayout = view.findViewById(R.id.constraintlayout);

    }
    private void KeyboardEvent(){
       KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
           @Override
           public void onVisibilityChanged(boolean isOpen) {
               if(!isOpen){
                   listResult.clear();
                   searchView.notifyDataSetChanged();
               }
           }
       });
    }
    private void AcceptControl(){
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                itemBill.notifyDataSetChanged();
                Log.e("-----------------","=================");
                final String codeClient = VNCharacterUtils.replaceWhiteSpace(editTextID.getText().toString().trim());
                String nameClient = editTextName.getText().toString().trim();
                String addressClient = editAddress.getText().toString().trim();
                String telecomClient = editTelecom.getText().toString().trim();
//tính giá
                ArrayList<BillOfSale> billOfSaleLists = itemBill.getData();
                Double cost = 0.0;

                final ArrayList<JSONObject> databill= new ArrayList<>();
                try {
                    for(int i = 0;i<billOfSaleLists.size();i++) {
                        JSONObject data = new JSONObject();
                        final Double total_amount = Double.parseDouble(billOfSaleLists.get(i).getTotal_amount());
                        data.put("categories", billOfSaleLists.get(i).getCategories().toString().trim());
                        data.put("types", billOfSaleLists.get(i).getType().toString().trim());
                        data.put("unit_price", billOfSaleLists.get(i).getUnit_price().toString().trim());
                        data.put("unit", billOfSaleLists.get(i).getUnit().toString().trim());
                        data.put("total_amount",billOfSaleLists.get(i).getTotal_amount().toString().trim());
                        data.put("date",billOfSaleLists.get(i).getDate().toString().trim());
                        data.put("quantity",billOfSaleLists.get(i).getQuantity().toString().trim());
                        data.put("note",billOfSaleLists.get(i).getNote().toString().trim());
                        data.put("construction_address",billOfSaleLists.get(i).getAddress().toString().trim());
                        cost = cost+total_amount;
                        totalCost.setText(currencyVN.format((cost)));
                        databill.add(data);
                    }
                }catch (Exception e){
                    Log.e("JSONException",e.toString());
                }
                final JSONArray pay = new JSONArray(databill);
                if(codeClient.equals("")){
                    editTextID.setError("Không được bỏ trống mã khách");
                    editTextID.requestFocus();
                    return;
                }
                if(nameClient.equals("")){
                    editTextName.setError("Không được bỏ trống tên khách");
                    editTextName.requestFocus();
                    return;
                }

                JSONObject jsonObject =  new JSONObject();
                try {
                    jsonObject.put("name",nameClient);
                    jsonObject.put("address",addressClient);
                    jsonObject.put("telephone",telecomClient);
                    jsonObject.put("code",codeClient);
                    jsonObject.put("status","pending");
                    jsonObject.put("bill",pay.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                final AsyntaskAPI createBill =new AsyntaskAPI(getContext(),jsonObject,ConfigAPI.API_BILL,"POST",token) {
                    @Override
                    public void setOnPreExcute() {
                        btnAddBill.setEnabled(false);
                    }

                    @Override
                    public void setOnPostExcute(String JsonResult) {
                        Log.e("JsonResult",JsonResult);
                        btnAddBill.setEnabled(true);
                        try {
                           JSONObject rs =  new JSONObject(JsonResult);
                           if(!rs.toString().equals("") && rs.getString("message").equals("Successfully")){
                               Toast.makeText(getContext(),"Thêm hóa đơn cho mã khách "+codeClient+" thành công",Toast.LENGTH_LONG).show();
                               Clear();
                               return;
                           }
                           if(!rs.toString().equals("") && rs.getString("message").equals("pay_enough")){
                               Toast.makeText(getContext(),"Thanh toán hóa đơn cho mã khách "+codeClient+" thành công",Toast.LENGTH_LONG).show();
                               Clear();
                               return;
                           }
                            if(!rs.toString().equals("") && rs.getString("message").equals("Server Error")){
                               Toast.makeText(getContext(),"Vui lòng kiểm tra lại",Toast.LENGTH_LONG).show();
                               return;
                           }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Thông báo đơn giá")
                        .setMessage("Tổng giá là :"+currencyVN.format((cost)) + " ")
                        .setNegativeButton("Xem lại hóa đơn", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(ToolsCheck.checkInternetConnection(getContext())){
                                    createBill.execute();
                                }
                            }
                        })
                        .create();
                dialog.show();
            }

        });
}

    void Clear(){
        billOfSales.clear();
        itemBill.notifyDataSetChanged();
        editTextID.setText("");
        editAddress.setText("");
        editTelecom.setText("");
        editTextName.setText("");
        totalCost.setText(currencyVN.format(0));
        lastAddress = "";
    }

    private void RecycleViewControl(){
        itemBill = new ItemBills(getContext(), billOfSales);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvItem.setLayoutManager(mLayoutManager);
        rvItem.setAdapter(itemBill);
        btnAddBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.item_bil_of_sale, null);
                final TextView tenhang = (TextView)alertLayout.findViewById(R.id.tenhang);
                final CheckBox check = (CheckBox) alertLayout.findViewById(R.id.check);
                final TextView dvTinh  = (TextView)alertLayout.findViewById(R.id.dvTinh);
                final TextView loaihang  = (TextView)alertLayout.findViewById(R.id.loaihang);
                final EditText soluong  = (EditText)alertLayout.findViewById(R.id.soluong);
                final CurrencyEditText don_gia  = (CurrencyEditText)alertLayout.findViewById(R.id.dongia);
                final EditText note  = (EditText)alertLayout.findViewById(R.id.note);
                final TextView tvTime  = (TextView) alertLayout.findViewById(R.id.tvTime);
                final CurrencyEditText costBill  = (CurrencyEditText) alertLayout.findViewById(R.id.costBill);
                final TextView diachi  = (TextView) alertLayout.findViewById(R.id.diachi);
                if(!lastAddress.equals("")){
                    diachi.setText(lastAddress.trim());
                }

                Button btnAdd  = (Button)alertLayout.findViewById(R.id.btnAdd);
                Button btnSub  = (Button)alertLayout.findViewById(R.id.btnSub);
                dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = year+"-"+month+"-"+dayOfMonth;
                        tvTime.setText(date);
                        tvTime.setTextColor(Color.BLACK);
                    }
                };

                tvTime.setOnClickListener(new View.OnClickListener() {
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

                don_gia.addTextChangedListener(billPay(soluong,don_gia,costBill));
                soluong.addTextChangedListener(billPay(soluong,don_gia,costBill));

                ArrayList<String> list_tenhang = ChipChip(new SaveDataSHP(getContext()).getCate());
                ShowPopup(tenhang,list_tenhang);

                ArrayList<String> list_dvTinh = ChipChip(new SaveDataSHP(getContext()).getUnit());
                ShowPopup(dvTinh,list_dvTinh);

                ArrayList<String> list_loaihang = ChipChip(new SaveDataSHP(getContext()).getType());
                ShowPopup(loaihang,list_loaihang);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int temp = Integer.parseInt(soluong.getText().toString().trim());
                        soluong.setText(Integer.toString(temp+1));
                    }
                });
                btnSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int temp = Integer.parseInt(soluong.getText().toString().trim())-1;
                        if(temp>=0){
                            soluong.setText(Integer.toString(temp));
                        }
                    }
                });
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Thêm hóa đơn");
                alert.setView(alertLayout);
                alert.setCancelable(false);
                alert.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alert.setPositiveButton("Thêm Hóa Đơn", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tvTenhang = tenhang.getText().toString().trim();
                        String tvLoaihang = loaihang.getText().toString().trim();
                        String tvDvTinh = dvTinh.getText().toString().trim().equals("")?"cái":dvTinh.getText().toString().trim();
                        String tvSoluong = soluong.getText().toString().trim().equals("")?"0":soluong.getText().toString().trim();
                        String tvDongia = don_gia.getText().toString().trim().replace(",","");
                        String total = costBill.getText().toString().trim().equals("")?"0":costBill.getText().toString().trim().replace(",","");
                        String tvNote = note.getText().toString().trim();
                        String tvThoigian = tvTime.getText().toString().trim();
                        String tvDiachi = diachi.getText().toString().trim();
                        if(check.isChecked()){
//                            Log.e("total",total);
//                            Double temp = -Double.parseDouble(total);
//                            total = Double.toString(temp);
                            total = '-'+total;
                        }
                        if(!tvDiachi.equals("")){
                            lastAddress = tvDiachi;
                        }
                        if(tvThoigian.equals("")){
                            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            tvThoigian = currentDate;
                        }
                        if(!tvTenhang.equals("")){
                            BillOfSale a = new BillOfSale(tvThoigian,tvDiachi,tvTenhang,tvLoaihang,tvDvTinh,tvDongia,tvSoluong,total,tvNote);
                            billOfSales.add(a);
                            itemBill.notifyDataSetChanged();
                            setPay();
                        }else {
                            Toast.makeText(getContext(),"Hóa đơn không có Tên hàng hóa",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();

            }
        });
    }

    private  void ShowPopup(final TextView textView, final ArrayList<String> list){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(getContext(),textView);
                for (int i = 0;i<list.size();++i){
                    popup.getMenu().add(i,MENU+i,i,list.get(i));
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        textView.setText(item.getTitle());
                        textView.setTextColor(Color.BLACK);
                        return false;
                    }
                });
                popup.show();
            }
        });
    }
    private void SwipeToDeleteAndUndo(){
        SwipeToDeleteCallback swipeToDeleteCallback =new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final BillOfSale item = itemBill.getData().get(position);
                itemBill.removeItem(position);
                setPay();
                Snackbar snackbar = Snackbar
                        .make(constraintlayout,"Xóa khỏi danh sách",Snackbar.LENGTH_LONG);
                snackbar.setAction("HOÀN TÁC", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemBill.restoreItem(item,position);
                        rvItem.scrollToPosition(position);
                        itemBill.notifyDataSetChanged();
                        setPay();
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };
        itemBill.notifyDataSetChanged();
        setPay();
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvItem);

    }
    private ArrayList<String> ChipChip(String q){
        String [] chips = q.split(",");
        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(chips));
        ArrayList<String> result = new ArrayList<String>();
        for(String a:arrayList ){
            if(!result.contains(a) && !a.trim().equals("")){
                result.add(a);
            }
        }
        return result;

    }
    public void setPay(){
        totalCost.setText(currencyVN.format((0)));
        ArrayList<BillOfSale> billOfSaleLists = itemBill.getData();
        Double cost = 0.0;
        try {
            for(int i = 0;i<billOfSaleLists.size();i++) {
                JSONObject data = new JSONObject();
                final Double total_amount =Double.parseDouble(billOfSaleLists.get(i).getTotal_amount());
                cost = cost+total_amount;
                totalCost.setText(currencyVN.format((cost)));
            }
        }catch (Exception e){
            Log.e("JSONException",e.toString());
        }
    }
    private  TextWatcher billPay(final EditText soluong,final CurrencyEditText don_gia,final TextView costBill){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyVN).getDecimalFormatSymbols();
                decimalFormatSymbols.setCurrencySymbol("");
                ((DecimalFormat) currencyVN).setDecimalFormatSymbols(decimalFormatSymbols);
                if(!s.equals("")){
                    String sl = soluong.getText().toString().trim();
                    String dg = don_gia.getText().toString().trim().replace(",","");
                    if(sl.equals("") || dg.equals("")){
                        costBill.setText("0");
                    }else {
                        final Long total =Long.parseLong(sl)*Long.parseLong(dg);
                        String totalcost  = currencyVN.format(total).replace(".",",").replaceAll(" ", "");
                        costBill.setText(totalcost);
                    }
                }
            }
        };

    }
}

