package com.vn.quanly.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.adapter.Interface.connectCustomerActivity;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.BillOfSale;
import com.vn.quanly.ui.CurrencyEditText;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InfoClient extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_TITLE = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = 2;
    private List<BillOfSale> billList;
    private List<BillOfSale> tempList = new ArrayList<>();
    private boolean isLoading;
    private boolean isExport = false;
    boolean deleting= false;
    boolean checkAll =  false;
    private PopupMenu popup;
    private connectCustomerActivity connectCustomerActivity;
    private Context context;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

    public InfoClient(RecyclerView recyclerView, Context context, List<BillOfSale> billList){
        this.context = context;
        this.billList = billList;
        this.tempList = billList;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyVN).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) currencyVN).setDecimalFormatSymbols(decimalFormatSymbols);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_TITLE:{
                View view = LayoutInflater.from(context).inflate(R.layout.title_customer, parent, false);
                return new InfoClient.Title(view);
            }
            case VIEW_TYPE_ITEM: {
                View view = LayoutInflater.from(context).inflate(R.layout.item_bill, parent, false);
                return new InfoClient.ItemBill(view);
            }
            case VIEW_TYPE_LOADING: {
                View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
                return new InfoClient.LoadingView(view);
            }
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        //////////////////////////////////////////////////////////////TITLE KHÁCH HÀNG///////////////////////////////////////////////////////////////
        if(holder instanceof Title){
            SaveDataSHP saveDataSHP = new SaveDataSHP(context);
            String name = saveDataSHP.getString("name_client");
            String address = saveDataSHP.getString("address_client");
            String money_limit = saveDataSHP.getString("money_limit_client");
            String date_limit = saveDataSHP.getString("date_limit_client");
            String total = saveDataSHP.getString("total_client");
            String telephone =saveDataSHP.getString("telephone_client");
            ((Title) holder).customer.setText(name);
            ((Title) holder).tvAdress.setText("Địa chỉ :"+ address);

            if(ToolsCheck.isNumeric(money_limit))
            {
                ((Title) holder).tvDebt_lim.setText(currencyVN.format(Long.parseLong(money_limit)));
            }else {
                ((Title) holder).tvDebt_lim.setText(money_limit);
            }

            if(ToolsCheck.isNumeric(total))
            {
                ((Title) holder).total.setText( "Tổng dư nợ :"+(currencyVN.format(Long.parseLong(total))+""));
            }else {
                ((Title) holder).total.setText("Tổng dư nợ : Không xác định");
            }
            if(!new SaveDataSHP(context).getString(SaveDataSHP.SHP_PROMISE).equals("1")){
                ((Title) holder).update.setVisibility(View.INVISIBLE);
            }

            if(ToolsCheck.validatePhoneNumber(telephone)){
                ((Title) holder).tvTelecom.setText( "Số Điện Thoại :"+telephone);
            }else {
                ((Title) holder).tvTelecom.setText( "Số Điện Thoại :");
            }

            ((Title) holder).tvTime.setText(date_limit);
            ((Title) holder).update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectCustomerActivity.ShowDialog();
                }
            });
            ((Title) holder).exportFile.setImageDrawable(!isExport?
                    ContextCompat.getDrawable(context,R.drawable.ic_attachment_file):
                    ContextCompat.getDrawable(context,R.drawable.ic_cancel));
            ((Title) holder).exportFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   //Hiện/ẩn btn savefile , checkbox item
                    isExport = !isExport;
                    notifyDataSetChanged();

                }
            });
            ((Title) holder).saveFile.setVisibility(isExport?View.VISIBLE:View.GONE);
//            ((Title) holder).checkAll.setVisibility(isExport?View.VISIBLE:View.GONE);
//            ((Title) holder).checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                   checkAll = isChecked;
//                   notifyDataSetChanged();
//                }
//            });
            ((Title) holder).saveFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   //lưu file excel -> ẩn btn
                    connectCustomerActivity.ExportFile(tempList);
                }
            });


        }
        //////////////////////////////////////////////////////////////////////THÔNG TIN HÓA ĐƠN /////////////////////////////////////////////////
        if(holder instanceof ItemBill){
            final BillOfSale bill = billList.get(position-1);
        ((ItemBill) holder).checkBox.setVisibility(isExport?View.VISIBLE:View.GONE);
        ((ItemBill) holder).btnOpion.setVisibility(!isExport?View.VISIBLE:View.GONE);
        ((ItemBill) holder).tvLoaiHang.setText( "Loại Hàng: "+bill.getType());
        ((ItemBill) holder).tvTenHang.setText("Tên Hàng: " +bill.getCategories());
        ((ItemBill) holder).tvDiachi.setText( bill.getAddress());
        ((ItemBill) holder).tvDiachi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBill(bill);
            }
        });
            ((ItemBill) holder).tvTotal.setText( currencyVN.format(Double.parseDouble(bill.getTotal_amount())));
            ((ItemBill) holder).tvTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBill(bill);
                }
            });

        ((ItemBill) holder).tvSoluong.setText("Số lượng : "+bill.getQuantity());
        ((ItemBill) holder).tvDonViTinh.setText("Đơn giá :"+ currencyVN.format(Long.parseLong(bill.getUnit_price()))+"/"+bill.getUnit());
        ((ItemBill) holder).btnOpion.setEnabled(!deleting);
        ((ItemBill) holder).btnOpion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(context,((ItemBill) holder).btnOpion);
                popup.getMenuInflater().inflate(R.menu.option,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                deleteBill(bill.getId(),position-1);
                                return false;
                            case R.id.update:
                                connectCustomerActivity.UpdateBill(bill,position-1);
                                return false;
                            default:return false;
                        }
                    }
                });
                if(Double.parseDouble(bill.getTotal_amount())<0){
                    popup.getMenu().findItem(R.id.update).setVisible(false);
                }
                popup.show();
            }
        });
            if(!new SaveDataSHP(context).getString(SaveDataSHP.SHP_PROMISE).equals("1")){
                ((ItemBill) holder).btnOpion.setVisibility(View.GONE);
            }
        ((ItemBill) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if(isChecked){
                        tempList.add(bill);
                    }else {
                        tempList.remove(tempList.indexOf(bill));
                    }
                }catch (IndexOutOfBoundsException e){
                    Log.e("error",e.toString());
                }

            }
        });
            ((ItemBill) holder).tvTime.setText(bill.getDate());
//            ((ItemBill) holder).checkBox.setChecked(checkAll);
        }
        if(holder instanceof LoadingView){

        }
    }

    public void setConnectCustomerActivity(connectCustomerActivity connectCustomerActivity) {
        this.connectCustomerActivity = connectCustomerActivity;
    }

    @Override
    public int getItemCount() {
        return billList == null?0:billList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position<=0){
            return 0;
        }else {
            return billList.get(position-1)==null ?VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
        }
    }

    private class LoadingView extends RecyclerView.ViewHolder{
        ProgressBar progress_circular;
        public LoadingView(@NonNull View itemView) {
            super(itemView);
            progress_circular = itemView.findViewById(R.id.progress_circular);
        }
    }
    private class ItemBill extends RecyclerView.ViewHolder{
        TextView tvTenHang;
        TextView tvLoaiHang;
        TextView tvSoluong;
        TextView tvDonViTinh;
        TextView tvDiachi;
        TextView tvTotal;
        TextView tvTime;
        ImageView btnOpion;
        CheckBox checkBox;

        public ItemBill(@NonNull View itemView) {
            super(itemView);
            tvTenHang = itemView.findViewById(R.id.tvTenHang);
            tvLoaiHang = itemView.findViewById(R.id.tvLoaiHang);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSoluong = itemView.findViewById(R.id.tvSoluong);
            tvDonViTinh = itemView.findViewById(R.id.tvDonViTinh);
            tvDiachi = itemView.findViewById(R.id.tvDiachi);
            btnOpion = itemView.findViewById(R.id.btnOption);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
    private class Title extends RecyclerView.ViewHolder{
        TextView customer;
        TextView tvAdress;
        TextView tvTelecom;
        TextView tvDebt_lim;
        TextView tvTime;
        TextView total;
        ImageButton update;
        ImageButton exportFile;
        ImageButton saveFile;
        public Title(@NonNull View itemView) {
            super(itemView);
            customer = itemView.findViewById(R.id.customer);
            tvAdress = itemView.findViewById(R.id.tvAdress);
            tvTelecom = itemView.findViewById(R.id.tvTelecom);
            tvDebt_lim = itemView.findViewById(R.id.tvDebt_lim);
            tvTime = itemView.findViewById(R.id.tvTime);
            update = itemView.findViewById(R.id.update);
            exportFile = itemView.findViewById(R.id.exportFile);
            saveFile = itemView.findViewById(R.id.saveFile);
            total = itemView.findViewById(R.id.total);
        }
    }
    private void deleteBill(String id, final int index){
        AsyntaskAPI delete = new AsyntaskAPI(context,new JSONObject(), ConfigAPI.API_BILL_of_Client+"/" +id,"DELETE",new SaveDataSHP(context).getShpToken()) {
            @Override
            public void setOnPreExcute() {
             deleting = true;
            }

            @Override
            public void setOnPostExcute(String JsonResult) {
            deleting =false;
            Log.e("JsonResult",JsonResult);
                try {
                    JSONObject rs  = new JSONObject(JsonResult);
                    if(rs.getString("message").equals("Successfully")){
                        ChangeTotalMoney(rs.getString("total"));
                        billList.remove(index);
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        delete.execute();
    }
    public void ChangeValue(BillOfSale billOfSale, int index){
        this.billList.set(index,billOfSale);
        notifyDataSetChanged();
    }
    public void ChangeTotalMoney(String money){
     SaveDataSHP saveDataSHP = new SaveDataSHP(context);
     saveDataSHP.setData("total_client",money);
     notifyDataSetChanged();
    }
    private void showBill(BillOfSale bill) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View alertLayout = layoutInflater.inflate(R.layout.update_pay, null);
        final EditText edDiachiCT = alertLayout.findViewById(R.id.edDiachiCT);
        final EditText edLoạiHang = alertLayout.findViewById(R.id.edLoạiHang);
        final EditText edTenHang = alertLayout.findViewById(R.id.edTenHang);
        final EditText edSoLuong = alertLayout.findViewById(R.id.edLimTime);
        final EditText edNote = alertLayout.findViewById(R.id.edNote);
        final EditText edDonvi = alertLayout.findViewById(R.id.edDonvi);
        final CurrencyEditText edDongia = alertLayout.findViewById(R.id.edDongia);


        edDiachiCT.setText(bill.getAddress());
        edDiachiCT.setEnabled(false);
        edDiachiCT.setTextColor(Color.BLACK);

        edNote.setText(bill.getNote());
        edNote.setEnabled(false);
        edNote.setTextColor(Color.BLACK);

        edLoạiHang.setText(bill.getType());
        edLoạiHang.setEnabled(false);
        edLoạiHang.setTextColor(Color.BLACK);

        edTenHang.setText(bill.getCategories());
        edTenHang.setTextColor(Color.BLACK);
        edTenHang.setEnabled(false);

        edSoLuong.setText(bill.getQuantity());
        edSoLuong.setEnabled(false);
        edSoLuong.setTextColor(Color.BLACK);

        edDonvi.setText(bill.getUnit());
        edDonvi.setEnabled(false);
        edDonvi.setTextColor(Color.BLACK);

        edDongia.setText(bill.getUnit_price());
        edDongia.setEnabled(false);

        edDongia.setTextColor(Color.BLACK);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Thông tin hóa đơn");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        AlertDialog dialog = alert.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
