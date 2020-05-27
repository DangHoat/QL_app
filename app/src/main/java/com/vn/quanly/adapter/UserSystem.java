package com.vn.quanly.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.quanly.R;
import com.vn.quanly.api.AsyntaskAPI;
import com.vn.quanly.model.User;
import com.vn.quanly.ui.MainActivity;
import com.vn.quanly.ui.StartActivity;
import com.vn.quanly.utils.ConfigAPI;
import com.vn.quanly.utils.SaveDataSHP;
import com.vn.quanly.utils.ToolsCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSystem extends RecyclerView.Adapter<UserSystem.ViewHolder> {
    private ArrayList<User> listUser;
    private Context context;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    public  UserSystem(Context context, ArrayList<User> listUser){
        this.context = context;
        this.listUser = listUser;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new UserSystem.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final  User user = listUser.get(position);
        holder.email.setText(user.getEmail());
        holder.name.setText(user.getUsername());
        holder.switchAdmin.setChecked(user.getAdmin());
        if(!new SaveDataSHP(context).getString(SaveDataSHP.SHP_PROMISE).equals("1")){
            holder.switchAdmin.setEnabled(true);
        }
        if(new SaveDataSHP(context).getData(SaveDataSHP.SHP_EMAI).equals(user.getEmail())){
            holder.switchAdmin.setVisibility(View.GONE);
        }
        holder.switchAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn có chắc chắn với thay đổi này không?");
                builder.setTitle("Thay đổi quyền!");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChangPromise(isChecked,holder.email.getText().toString().trim());
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
    TextView email;
    TextView name;
    Switch switchAdmin;
    CircleImageView avatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.lastName);
            name = itemView.findViewById(R.id.name);
            switchAdmin = itemView.findViewById(R.id.switchAdmin);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
    private void ChangPromise(final Boolean promise, final String email){
        JSONObject data = new JSONObject();
        try {
            data.put("password",new SaveDataSHP(context).getData(SaveDataSHP.SHP_PASS));
            data.put("email",email);
            data.put("role",promise?"1":"2");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyntaskAPI changeRole = new AsyntaskAPI(context,data, ConfigAPI.API_CHANGE_ROLE,"PATCH",new SaveDataSHP(context).getShpToken()) {
            @Override
            public void setOnPreExcute() {

            }

            @Override
            public void setOnPostExcute(String JsonResult) {
                try {
                    Log.e("JsonResult",JsonResult);
                    JSONObject rs = new JSONObject(JsonResult);
                    if(!rs.toString().equals("")){
                        if(rs.getString("message").equals("Successfully")){
                            String role = promise?"Quản lý":"Nhân Viên";
                            Toast.makeText(context,"Thay đổi tài khoản" +email+ "thành "+role,Toast.LENGTH_SHORT).show();
                        }
                        if(!rs.toString().equals("")&& rs.getString("message").equals("Logout")){
                            new SaveDataSHP(context).removeSHP();
                            context.startActivity(new Intent(context, StartActivity.class));
                            return;
                        }
                        if(!rs.toString().equals("")&& rs.getString("message").equals("Fails")){
                            Toast.makeText(context,"Tài khoản "+email+ " không được cấp phép",Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                            return;
                        }
                        if(!rs.toString().equals("")&& rs.getString("message").equals("ErrorAu")){
                             new SaveDataSHP(context).setData(SaveDataSHP.SHP_PROMISE,"2");
                             context.startActivity(new Intent(context, MainActivity.class));

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        if(ToolsCheck.checkInternetConnection(context)){
            changeRole.execute();
        }else {
            notifyDataSetChanged();
        }
    }
}
