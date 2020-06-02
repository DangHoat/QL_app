package com.vn.quanly.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.adapter.InfoClient;
import com.vn.quanly.adapter.Interface.connectCustomerActivity;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.BillOfSale;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.ExcelExporter;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;
import com.vn.quanly.utils.VNCharacterUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<BillOfSale> billList = new ArrayList<>();
    private InfoClient infoCustomer;
    ActionBar actionBar;;
    DatePickerDialog.OnDateSetListener dateSetListener;
    Context context;
    SaveDataSHP saveDataSHP;
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
    private MenuItem item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context =this;
        setContentView(R.layout.activity_infomation_liabilities);
        Init();
        actionBar = getSupportActionBar();
        if(actionBar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        actionBar.setTitle(saveDataSHP.getString("name_client"));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(infoCustomer);
        final JSONObject data = new JSONObject();
        try {
            data.put("code",saveDataSHP.getString("code_client"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyntaskAPI getPay = new AsyntaskAPI(context,data, ConfigAPI.API_BILL_of_Client,"POST",saveDataSHP.getShpToken()) {
            @Override
            public void setOnPreExcute() {

            }

            @Override
            public void setOnPostExcute(String JsonResult) {
                try {
                    JSONArray rs = new JSONArray(JsonResult);
                    for (int i = 0;i<rs.length();i++){
                        JSONObject itemBill =  new JSONObject(rs.get(i).toString());
                        BillOfSale bill =  new BillOfSale(
                                Integer.toString(itemBill.getInt("id")),
                                itemBill.getString("date"),
                                itemBill.getString("construction_address"),
                                itemBill.getString("categories"),
                                itemBill.getString("types"),
                                itemBill.getString("unit"),
                                itemBill.getString("unit_price"),
                                itemBill.getString("quantity"),
                                itemBill.getString("total_amount"),
                                itemBill.getString("note")
                        );
                        billList.add(bill);
                    }

                        Collections.sort(billList, new Comparator<BillOfSale>() {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            @Override
                            public int compare(BillOfSale o1, BillOfSale o2) {
                                Date date1 = null;
                                Date date2 = null;
                                try {
                                    date1 = sdf.parse(o1.getDate());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    date2 = sdf.parse(o2.getDate());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return date1.compareTo(date2);
                            }
                        });
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(infoCustomer);
                    infoCustomer.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        if(ToolsCheck.checkInternetConnection(getApplicationContext())){
            getPay.execute();
        }
//        infoCustomer =  new InfoCustomer(recyclerView,getApplicationContext(),billList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setAdapter(infoCustomer);

        infoCustomer.setConnectCustomerActivity(new connectCustomerActivity() {
            @Override
            public void ShowDialog() {
             ShowDialogUpdateprofile();
            }

            @Override
            public void ExportFile(List<BillOfSale> billCustom) {
                try{
                    export( billCustom, VNCharacterUtils.replaceWhiteSpace(saveDataSHP.getString("code_client")));
                }
                catch (Exception e){
                }
            }
            @Override
            public void UpdateBill(BillOfSale billOfSale,int index) {
                updateBill(billOfSale,index);
            }
        });

    }
    private void Init(){
        recyclerView = findViewById(R.id.recyclerView);
        infoCustomer =  new InfoClient(recyclerView,context,billList);
        saveDataSHP = new SaveDataSHP(context);
    }
    private void updateBill(final BillOfSale billOfSale, final int index){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.update_pay, null);
        final EditText edDiachiCT = alertLayout.findViewById(R.id.edDiachiCT);
        final EditText edLoạiHang = alertLayout.findViewById(R.id.edLoạiHang);
        final EditText edNote = alertLayout.findViewById(R.id.edNote);
        final EditText edTenHang = alertLayout.findViewById(R.id.edTenHang);
        final EditText edSoLuong = alertLayout.findViewById(R.id.edLimTime);
        final EditText edDonvi = alertLayout.findViewById(R.id.edDonvi);
        final CurrencyEditText edDongia = alertLayout.findViewById(R.id.edDongia);


        edDiachiCT.setText(billOfSale.getAddress());
        edLoạiHang.setText(billOfSale.getType());
        edTenHang.setText(billOfSale.getCategories());
        edSoLuong.setText(billOfSale.getQuantity());
        edDonvi.setText(billOfSale.getUnit());
        edDongia.setText(billOfSale.getUnit_price());
        edNote.setText(billOfSale.getNote());


        AlertDialog.Builder alert = new AlertDialog.Builder(CustomerActivity.this);
        alert.setTitle("Thay đổi thông tin hóa đơn");
        alert.setView(alertLayout);
        alert.setCancelable(false);

        alert.setNegativeButton("Bỏ Qua", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton("Thay đổi ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //run asyn task
                final JSONObject data = new JSONObject();

                final Double total_amount =Double.parseDouble(edSoLuong.getText().toString().trim())*Double.parseDouble(edDongia.getText().toString().replace(",","").trim());
                try {

                    data.put("id", billOfSale.getId());
                    data.put("categories", edTenHang.getText().toString().trim());
                    data.put("note", edNote.getText().toString().trim());
                    data.put("types", edLoạiHang.getText().toString().trim());
                    data.put("unit_price",edDongia.getText().toString().replace(",","").replace(".","").trim());
                    data.put("unit", edDonvi.getText().toString().trim());
                    data.put("total_amount",total_amount);
                    data.put("date", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                    data.put("quantity",edSoLuong.getText().toString().trim());
                    data.put("construction_address",edDiachiCT.getText().toString().trim());
//                    data.put("tratruoc",tratruoc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final BillOfSale newbill = new BillOfSale(
                        billOfSale.getId(),
                        new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()),
                        edDiachiCT.getText().toString().trim(),
                        edTenHang.getText().toString().trim(),
                        edLoạiHang.getText().toString().trim(),
                        edDonvi.getText().toString().trim(),
                        edDongia.getText().toString().replace(",","").trim(),
                        edSoLuong.getText().toString().trim(),
                        Double.toString(total_amount),
                        edNote.getText().toString().trim());

                AsyntaskAPI update = new AsyntaskAPI(context,data,ConfigAPI.API_BILL,"PUT",saveDataSHP.getShpToken()) {
                    @Override
                    public void setOnPreExcute() {

                    }

                    @Override
                    public void setOnPostExcute(String JsonResult) {
                       ;
                        try {
                            JSONObject rs  = new JSONObject(JsonResult);
                            if(rs.getString("message").equals("Successfully")){
                               infoCustomer.ChangeValue(newbill,index);
                               infoCustomer.ChangeTotalMoney(rs.getString("total"));
                               infoCustomer.notifyDataSetChanged();
                               Toast.makeText(context,"Thay đổi thông tin đơn hàng",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context,"Xảy ra lỗi.Vui lòng kiểm tra lại!",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                update.execute();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

    }
    private void ShowDialogUpdateprofile(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.options_menu, null);
        final EditText edTenkhachhang = alertLayout.findViewById(R.id.edDiachiCT);
        final EditText edDiachi = alertLayout.findViewById(R.id.edLoạiHang);
        final EditText edSodienthoai = alertLayout.findViewById(R.id.edTenHang);
        final EditText edTratruoc = alertLayout.findViewById(R.id.edNote);
        final EditText edHanmuc = alertLayout.findViewById(R.id.edDonvi);
        final EditText limTime =alertLayout.findViewById(R.id.edLimTime);

        edTenkhachhang.setText(saveDataSHP.getString("name_client"));
        edDiachi.setText(saveDataSHP.getString("address_client"));
        edSodienthoai.setText(saveDataSHP.getString("telephone_client"));
        edTratruoc.setText(saveDataSHP.getString(""));
        if(ToolsCheck.isNumeric(saveDataSHP.getString("money_limit_client"))){
            edHanmuc.setText(saveDataSHP.getString("money_limit_client"));
        }
        if(!saveDataSHP.getString("date_limit_client").equals("Không hạn ngày")) {
            limTime.setText(saveDataSHP.getString("date_limit_client"));
        }
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

                DatePickerDialog dialog = new DatePickerDialog(CustomerActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(CustomerActivity.this);
        alert.setTitle("Thay đổi thông tin khách hàng");
        alert.setView(alertLayout);
        alert.setCancelable(false);

        alert.setNegativeButton("Bỏ Qua", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setPositiveButton("Thay đổi thông tin ", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String tenkhachhang = edTenkhachhang.getText().toString().trim();
                final String diachi = edDiachi.getText().toString().trim();
                final String sodienthoai = edSodienthoai.getText().toString().trim();
                final String hanmuc = edHanmuc.getText().toString().trim().replace(",","");
                String tratruoc = edTratruoc.getText().toString().trim();
                final String limitTime = limTime.getText().toString().trim().equals("")?"":limTime.getText().toString().trim();
                JSONObject data = new JSONObject();
                try {
                    data.put("code",new SaveDataSHP(getApplicationContext()).getData("code_client"));
                    data.put("name",tenkhachhang);
                    data.put("address",diachi);
                    data.put("telephone",sodienthoai);
                    data.put("money_limit",hanmuc);
//                    data.put("tratruoc",tratruoc);
                    data.put("date_limit",limitTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AsyntaskAPI changeClient = new AsyntaskAPI(CustomerActivity.this
                        ,data,ConfigAPI.API_CLIENT,"PATCH",new SaveDataSHP(getApplicationContext()).getShpToken()) {
                    @Override
                    public void setOnPreExcute() {

                    }

                    @Override
                    public void setOnPostExcute(String JsonResult) {
                        Log.e("JsonResult",JsonResult);
                        try {
                            JSONObject rs = new JSONObject(JsonResult);
                            if (!rs.toString().equals("") && rs.getString("message").equals("Successfully")) {
                                SharedPreferences sharedPreferences = getSharedPreferences(SaveDataSHP.SHP,MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name_client", tenkhachhang);
                                editor.putString("address_client", diachi);
                                editor.putString("telephone_client", sodienthoai);
                                if(!limitTime.equals("")){
                                    editor.putString("date_limit_client",limitTime);
                                }
                                if(!hanmuc.equals("")){
                                    editor.putString("money_limit_client", hanmuc);
                                }

                                editor.commit();

                                Toast.makeText(CustomerActivity.this, "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show();
                                infoCustomer.notifyDataSetChanged();
                            }else {
                                Toast.makeText(CustomerActivity.this, "Hãy kiểm tra lại", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                if(ToolsCheck.checkInternetConnection(getApplicationContext())){
                    changeClient.execute();
                }

                dialog.dismiss();

            }
        });
        AlertDialog dialog = alert.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void export(final List<BillOfSale> billList, final String filename){
        new ToolsCheck().askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1,this);
        new ToolsCheck().askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1,this);
        //generate data
       final String file = filename +"_" +new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        StringBuilder data = new StringBuilder();
        Double cost = 0.0;
        data.append("STT,N/T,Địa Chỉ Công Trình,Hạng Mục,Chủng Loại hàng,ĐVT,Số Lượng,Đơn Giá (VNĐ) ,Thành Tiền (VNĐ) ,Ghi chú");
        for(int i = 0; i<billList.size(); i++){
            final   BillOfSale billOfSale = billList.get(i);
            cost += Double.parseDouble(billOfSale.getQuantity())*Double.parseDouble(billOfSale.getUnit_price());
            data.append("\n"+Integer.toString(i)+","  //STT
                    +billOfSale.getDate()+"," //N/T
                    +billOfSale.getAddress()+","       //Địa chỉ
                    +billOfSale.getCategories()+"," //Hạng mục
                    +billOfSale.getType()+","       //Chủng Loại
                    +billOfSale.getUnit()+","       //ĐVT
                    +currencyVN.format(Double.parseDouble(billOfSale.getQuantity()))+","   //Số Lượng
                    +currencyVN.format(Double.parseDouble(billOfSale.getUnit_price()))+"," //Đơn Giá
                    +currencyVN.format(Double.parseDouble(billOfSale.getQuantity())*Double.parseDouble(billOfSale.getUnit_price()))+","  //Thành tiền
                    +billOfSale.getNote());//Ghi chú
        }

        data.append("\n");
        data.offsetByCodePoints(1,5);
        data.append(",");
        data.append(",");
        data.append("TỔNG TIỀN  :");
        data.append(",");
        data.append(",");
        data.append(",");
        data.append(",");
        data.append(",");
        data.append(currencyVN.format(cost));

        try{
            //saving the file into device
            FileOutputStream out = openFileOutput(file+".csv", MODE_APPEND);
            out.write((data.toString()).getBytes());
            out.close();
            //exporting
            final Context context = getApplicationContext();
            final  File filelocation = new File(getFilesDir(), file+".csv");
            final Uri path = FileProvider.getUriForFile(context, "com.vn.quanly.fileprovider", filelocation);;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Trích xuất file");
            builder.setMessage("Chọn hành động để trích xuất");
            // add the buttons

            builder.setPositiveButton("Gửi đi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent fileIntent = new Intent(Intent.ACTION_SEND);
                    fileIntent.setType("text/csv");
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT, file );
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                    startActivity(Intent.createChooser(fileIntent, "Gửi bản hóa đơn"));
                }
            });

            builder.setNegativeButton("Xem trước", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ExcelExporter.export(getApplicationContext(),file,billList);
                    Toast.makeText(context,"Tạo bảng thành công kiểm tra trong thư mục",Toast.LENGTH_SHORT).show();

                }
            });
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();


        }
        catch(Exception e){
            e.printStackTrace();
             Log.e("errrrrrrrrrrrrrr",e.toString());
        }

    }



    @SuppressLint("WrongConstant")
    private void showFile(File file, String filetype)
    {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("file/csv");
        String mimeType =
                myMime.getMimeTypeFromExtension(filetype);

        if(android.os.Build.VERSION.SDK_INT >=24) {
            Uri fileURI = FileProvider.getUriForFile(context,
                    "com.vn.quanly.fileprovider",
                    file);
            intent.setDataAndType(fileURI, mimeType);

        }else {
            intent.setDataAndType(Uri.fromFile(file), mimeType);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(context, "No Application found to open this type of file.", Toast.LENGTH_LONG).show();

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.payin:
                break;

            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    void payin(){
        final CurrencyEditText input = (CurrencyEditText) new CurrencyEditText(this);
        input.setHint("Thanh toán trước");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(saveDataSHP.getString("name_client"));
        builder.setView(input);
        builder.setNegativeButton("Bỏ Qua", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Thanh Toán", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                String task = String.valueOf(input.getText());
                if(!task.equals("")){
                    task = task.replace(",","");
                }else {
                    return;
                }
                JSONObject data =  new JSONObject();
                try {
                    data.put("money",Double.parseDouble(task));
                    data.put("code",saveDataSHP.getString("code_client"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new AsyntaskAPI(context, data, ConfigAPI.API_CLIENT, "PUT",saveDataSHP.getShpToken()) {
                    @Override
                    public void setOnPreExcute() {

                    }

                    @Override
                    public void setOnPostExcute(String JsonResult) {
                        Log.e("Thanh Toan",JsonResult);
                        try {
                            JSONObject rs  = new JSONObject(JsonResult);
                            if(rs.getString("message").equals("Successfully")){
                                Toast.makeText(context,"Đã thanh toán",Toast.LENGTH_SHORT).show();
                                infoCustomer.ChangeTotalMoney(rs.getString("total"));
                                return;
                            }
                            if(rs.getString("message").equals("pay_enough")){
                                Toast.makeText(context,"Khách hàng không có dư nợ",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(rs.getString("message").equals("Error")){
                                Toast.makeText(context,"Bạn không được quyền thay đôi thông tin thanh toán!",Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }
        });
        AlertDialog dialog = builder
                .create();
        dialog.show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.pay_in,menu);
//        return true;
//    }

}
