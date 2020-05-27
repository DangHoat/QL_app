package com.vn.quanly.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.adapter.Interface.recycleViewAction;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.PayBook;
import com.vn.quanly.ui.CustomerActivity;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;
import com.vn.quanly.utils.VNCharacterUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemPayBooks extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_HIDE = 1;
    private boolean isLoading;
    private List<PayBook> payBooksFull;
    private List<PayBook> payBooksFilter;
    private Context context;
    private RecyclerView recyclerView;
    private recycleViewAction recycleViewAction;
    private int lastVisibleItem, totalItemCount;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);


    public ItemPayBooks(Context context, RecyclerView recyclerView, List<PayBook> payBooks, final int itemAddAfterMoreLoad ){
        this.context = context;
        this.recyclerView = recyclerView;
        this.payBooksFilter = payBooks;
        this.payBooksFull = new ArrayList<>(payBooks);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if(!isLoading ){
                        if(recycleViewAction!=null){
                            recycleViewAction.loadMore();
                        }
                        isLoading = true;
                    }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_ITEM: {
                View view = LayoutInflater.from(context).inflate(R.layout.item_paybook, parent, false);
                return new ItemPayBookView(view);
            }
            case VIEW_HIDE: {
                View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
                return new LoadingView(view);
            }
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if(holder instanceof ItemPayBookView){
                ((ItemPayBookView) holder).customer.setText(payBooksFilter.get(position).getCustomer());
                ((ItemPayBookView) holder).code.setText(payBooksFilter.get(position).getCodeClient());
                if(ToolsCheck.isNumeric(payBooksFilter.get(position).getTotal())){
                    ((ItemPayBookView) holder).money.setText(currencyVN.format(Long.parseLong(payBooksFilter.get(position).getTotal())));
                }else {
                    ((ItemPayBookView) holder).money.setText(currencyVN.format(Long.parseLong("0")));
                }
                ;
                ((ItemPayBookView) holder).btnShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoCustom(payBooksFilter.get(position).getCodeClient());
                    }
                });
            }else if(holder instanceof LoadingView ){
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

    }

    @Override
    public int getItemCount() {
        return  payBooksFilter == null?0:payBooksFilter.size();
    }

    @Override
    public int getItemViewType(int position) {
        return payBooksFilter.get(position).getIsPay()?VIEW_TYPE_ITEM:VIEW_HIDE;
    }

    public void setRecycleViewAction(com.vn.quanly.adapter.Interface.recycleViewAction recycleViewAction) {
        this.recycleViewAction = recycleViewAction;
    }

    public void setLoading() {
        isLoading = false;
    }
    public boolean getLoading(){
        return this.isLoading;
    }

    private class LoadingView extends RecyclerView.ViewHolder{
        ProgressBar progress_circular;
        public LoadingView(@NonNull View itemView) {
            super(itemView);
            progress_circular = itemView.findViewById(R.id.progress_circular);
        }
    }
    private class ItemPayBookView extends RecyclerView.ViewHolder{
        TextView customer;
        TextView money;
        TextView code;
//        TextView debt_lim;
        ImageButton btnShow;
        public ItemPayBookView(@NonNull View itemView) {
            super(itemView);
            customer = itemView.findViewById(R.id.customer);
            code = itemView.findViewById(R.id.code);
            money = itemView.findViewById(R.id.timedate);
//            debt_lim = itemView.findViewById(R.id.debt_lim);
            btnShow = itemView.findViewById(R.id.btnShow);
        }
    }
    @Override
    public Filter getFilter() {
        return dataFilter;
    }
    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<PayBook> list = new ArrayList<>();
            if(constraint.toString().trim().isEmpty()){
                list.addAll(payBooksFull);
//                notifyDataSetChanged();
            }else {
                String textFilter = VNCharacterUtils.removeAccent(constraint.toString().toLowerCase().trim());
                List<PayBook> filteredList = new ArrayList<>();
                for (PayBook item : payBooksFull){
                    if(VNCharacterUtils.removeAccent(item.getCodeClient().toLowerCase().trim()).contains(textFilter)){
                        filteredList.add(item);
                    }
                }
                list = filteredList;
            }
            FilterResults results = new FilterResults();
            results.values = list;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            payBooksFilter.clear();
//            if((List)results.values!=null){
//                payBooksFilter.addAll((List)results.values);
//            }
            payBooksFilter = (ArrayList<PayBook>) results.values;
            notifyDataSetChanged();
        }
    };

    public void gotoCustom(String client){
        JSONObject data = new JSONObject();
        try {
            data.put("code",client);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AsyntaskAPI getClient = new AsyntaskAPI(context,data,ConfigAPI.API_CLIENT+"/getClientByCode","POST",new SaveDataSHP(context).getShpToken()) {
            @Override
            public void setOnPreExcute() {

            }

            @Override
            public void setOnPostExcute(String JsonResult) {
                try {
                    JSONObject rs = new JSONObject(JsonResult);
                    JSONArray data = rs.getJSONArray("info");
                    JSONObject client = data.getJSONObject(0);
                    new SaveDataSHP(context).setClient(client);
                    Intent intent = new Intent (context, CustomerActivity.class);
                    context.startActivities(new Intent[]{intent});
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        getClient.execute();
    }
}
