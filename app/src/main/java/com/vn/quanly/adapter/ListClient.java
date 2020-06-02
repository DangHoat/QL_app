package com.vn.quanly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.adapter.Interface.deleteClient;
import com.vn.quanly.model.Client;
import com.vn.quanly.utils.SaveDataSHP;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListClient extends RecyclerView.Adapter<ListClient.ViewHolder> {
    private List<Client> listClients;
    private Context context;
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
    private com.vn.quanly.adapter.Interface.deleteClient deleteClient;
    public ListClient(Context context, List<Client> listClients){
        this.context = context;
        this.listClients = listClients;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_client,parent,false);
        return new ListClient.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Client client  = listClients.get(position);
        holder.title_client.setText(client.getCode() +" - "+ currencyVN.format(Double.parseDouble(client.getTotal())));
        holder.delete_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClient.delete(client);
            }
        });
        if(!new SaveDataSHP(context).getString(SaveDataSHP.SHP_PROMISE).equals("1")){
            holder.delete_client.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listClients.size();
    }

    public void setDeleteClient(com.vn.quanly.adapter.Interface.deleteClient deleteClient) {
        this.deleteClient = deleteClient;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView title_client;
       ImageButton delete_client;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title_client = itemView.findViewById(R.id.title_client);
            delete_client = itemView.findViewById(R.id.delete_client);
        }
    }
}
