package com.example.apicalls;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    LayoutInflater inflater;
    List<Users> users;

    public Adapter(Context ctx, List<Users> users){
        this.inflater = LayoutInflater.from(ctx);
        this.users = users;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userFull.setText(users.get(position).getFullName());
        holder.userName.setText(users.get(position).getUserName());
        Picasso.get().load(users.get(position).getUrlImage()).into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userFull, userName;
        ImageView profileImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userFull = itemView.findViewById(R.id.Name);
            userName = itemView.findViewById(R.id.UserName);
            profileImage = itemView.findViewById(R.id.imageView);
        }
    }

}
