package com.vn.quanly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.adapter.Interface.recycleViewAction;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.Noitification;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemNoitifications extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private boolean isLoading;
    private List<Noitification> noitificationList;
    private RecyclerView recyclerView;
    private recycleViewAction recycleViewAction;
    private Context context;
    int totalItemCount,lastVisibleItem;
    private PopupMenu popup;
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

    public ItemNoitifications(Context context,RecyclerView recyclerView,List<Noitification> noitificationList){
        this.context = context;
        this.noitificationList = noitificationList;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalItemCount<lastVisibleItem ){
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
                View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
                return new ItemNoitifications.ItemNoitificationView(view);
            }
            case VIEW_TYPE_LOADING:{
                View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
                return new ItemNoitifications.LoadingView(view);
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ItemNoitifications.ItemNoitificationView){
            final Noitification noitification = noitificationList.get(position);
            ((ItemNoitificationView) holder).tvTenHang.setText(noitification.getTitle()+"(mã:"+noitification.getCode()+")đang có nợ");
            if(!noitification.getCheck()){
                ((ItemNoitificationView) holder).layout.setBackground(ContextCompat.getDrawable(context,R.drawable.border_bottom_gray));
            }else {
                ((ItemNoitificationView) holder).layout.setBackground(ContextCompat.getDrawable(context,R.drawable.border_bottom_white));
            }
            if(!new SaveDataSHP(context).getString(SaveDataSHP.SHP_PROMISE).equals("1")){
                ((ItemNoitificationView) holder).more.setVisibility(View.GONE);
            }
            ((ItemNoitificationView) holder).more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup = new PopupMenu(context,((ItemNoitificationView) holder).more);
                    popup.getMenuInflater().inflate(R.menu.option_notification,popup.getMenu());
//                    if(noitification.getCheck()){
//                        popup.getMenu().findItem(R.id.watch).setVisible(true);
//                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
//                                case R.id.watch:
//                                    ((ItemNoitificationView) holder).layout.setBackground(ContextCompat.getDrawable(context,R.drawable.border_bottom_white));
//                                    noitification.setCheck(true);
//                                    return false;
                                case R.id.delete:
                                    AsyntaskAPI deleteNoi = new AsyntaskAPI(context, ConfigAPI.API_DELETE_OWE+noitification.getCode(),new SaveDataSHP(context).getShpToken(),false) {
                                        @Override
                                        public void setOnPreExcute() {

                                        }

                                        @Override
                                        public void setOnPostExcute(String JsonResult) {
                                            JSONObject rs = null;
                                            try {
                                                rs = new JSONObject(JsonResult);
                                                if (!rs.toString().equals("") && rs.getString("message").equals("Successfully")) {
                                                    removeItem(position);
                                                    notifyDataSetChanged();
                                                    Toast.makeText(context,"Đã xác nhận thanh toán nợ",Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }



                                        }
                                    };
                                    if(ToolsCheck.checkInternetConnection(context)){
                                        deleteNoi.execute();
                                    }
                                    return false;
                                default:return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
            ((ItemNoitificationView) holder).tvTime.setText(noitification.getTime());

            if(ToolsCheck.isNumeric(noitification.getMoney())){
                ((ItemNoitificationView) holder).tvMoney.setText(currencyVN.format(Long.parseLong(noitification.getMoney())));
            }else {
                ((ItemNoitificationView) holder).tvMoney.setText(noitification.getMoney());
            }


        }else if(holder instanceof ItemNoitifications.LoadingView){
            ItemNoitifications.LoadingView loadingView = (ItemNoitifications.LoadingView) holder;
            loadingView.progress_circular.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return noitificationList==null?0:noitificationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return noitificationList.get(position)==null?VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public void setRecycleViewAction(com.vn.quanly.adapter.Interface.recycleViewAction recycleViewAction) {
        this.recycleViewAction = recycleViewAction;
    }
    public void removeItem(int position) {

        if (position >= noitificationList.size())
            return;
        noitificationList.remove(position);
        notifyItemRemoved(position);
    }


    private class LoadingView extends RecyclerView.ViewHolder{
    ProgressBar progress_circular;
    public LoadingView(@NonNull View itemView) {
        super(itemView);
        progress_circular = itemView.findViewById(R.id.progress_circular);
    }
    }
    private class ItemNoitificationView extends RecyclerView.ViewHolder{
        CircleImageView logo;
        TextView tvTime;
        ImageView more;
        TextView tvTenHang;
        TextView tvMoney;
        ConstraintLayout layout;

    public ItemNoitificationView(@NonNull View itemView) {
        super(itemView);
            logo =itemView.findViewById(R.id.icon_noitification);
            tvTime =itemView.findViewById(R.id.tvTime);
            more =itemView.findViewById(R.id.more);
        tvMoney =itemView.findViewById(R.id.tvMoney);
            tvTenHang =itemView.findViewById(R.id.tvDiachi);
            layout =itemView.findViewById(R.id.layout);
        }
    }
}
