package com.vn.quanly.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.model.BillOfSale;
import com.vn.quanly.ui.CurrencyEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ItemBills extends RecyclerView.Adapter<ItemBills.ViewHolder> {
    private Context context;
    private ArrayList<BillOfSale> billOfSaleList;
    private com.vn.quanly.adapter.Interface.recycleViewAction recycleViewAction;

    public ItemBills(Context context, ArrayList<BillOfSale> billOfSaleList){
        this.context = context;
        this.billOfSaleList = billOfSaleList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bil_of_sale,parent,false);
        return new ItemBills.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final  BillOfSale billOfSale = billOfSaleList.get(position);
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        if(billOfSale.getUnit_price().equals("")){
            holder.don_gia.setText(currencyVN.format(Long.parseLong("0")));
        }else {
            holder.don_gia.setText(currencyVN.format(Double.parseDouble(billOfSale.getUnit_price())).toString().trim());
        }
        if(billOfSale.getQuantity().equals("")){
            holder.soluong.setText("0");
        }
        holder.btnAdd.setVisibility(View.GONE);
        holder.btnSub.setVisibility(View.GONE);
        holder.soluong.setText(billOfSale.getQuantity());

        holder.dvTinh.setText(billOfSale.getUnit());
        holder.dvTinh.setCompoundDrawables(null, null, null, null);

        holder.tenhang.setText(billOfSale.getCategories());
        holder.tenhang.setCompoundDrawables(null, null, null, null);

        holder.loaihang.setText(billOfSale.getType());
        holder.loaihang.setCompoundDrawables(null, null, null, null);

        holder.check.setVisibility(View.GONE);
        holder.note.setText(billOfSale.getNote());

        holder.tvTime.setText(billOfSale.getDate());
        holder.diachi.setText(billOfSale.getAddress());
        holder.don_gia.setEnabled(false);
        holder.soluong.setEnabled(false);
        holder.note.setEnabled(false);

        final Double total_amount =Double.parseDouble(billOfSale.getTotal_amount());
        if(total_amount!=0||!total_amount.equals("null")){
            holder.costBill.setText(currencyVN.format(total_amount));
        }else {
            holder.costBill.setText(currencyVN.format(0));
        }
    }
    public void removeItem(int position) {
        billOfSaleList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(BillOfSale item, int position) {
        billOfSaleList.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<BillOfSale> getData() {
        return billOfSaleList;
    }

    @Override
    public int getItemCount() {
        return billOfSaleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tenhang;
        TextView dvTinh;
        TextView loaihang;
        CurrencyEditText costBill;
        TextView tvTime;
        EditText soluong;
        EditText don_gia;
        EditText diachi;
        CheckBox check;
        EditText note;
        Button btnAdd;
        Button btnSub;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            loaihang = itemView.findViewById(R.id.loaihang);
            check = itemView.findViewById(R.id.check);
            tenhang = itemView.findViewById(R.id.tenhang);
            costBill = itemView.findViewById(R.id.costBill);
            dvTinh = itemView.findViewById(R.id.dvTinh);
            soluong = itemView.findViewById(R.id.soluong);
            don_gia = itemView.findViewById(R.id.dongia);
            note = itemView.findViewById(R.id.note);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnSub = itemView.findViewById(R.id.btnSub);
            tvTime = itemView.findViewById(R.id.tvTime);
            diachi = itemView.findViewById(R.id.diachi);
        }
    }
}
