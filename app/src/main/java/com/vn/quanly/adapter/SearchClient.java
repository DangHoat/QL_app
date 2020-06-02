package com.vn.quanly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.model.Client;

import java.util.List;

public class SearchClient extends RecyclerView.Adapter<SearchClient.ViewHolder> {
    Context context;
    List<Client> list;
    com.vn.quanly.adapter.Interface.clickItemSearch clickItemSearch;
    public SearchClient(Context context, List<Client> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search,parent,false);
        return new SearchClient.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvCode.setText(list.get(position).getCode() +"( Tên khách hàng : "+list.get(position).getName()+")");
        holder.tvCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItemSearch.onClick(list.get(position));
            }
        });
    }

    public void setClickItemSearch(com.vn.quanly.adapter.Interface.clickItemSearch clickItemSearch) {
        this.clickItemSearch = clickItemSearch;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
        }
    }
}
