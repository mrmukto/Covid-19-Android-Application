package com.shoutlab.coronabd.Myths;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shoutlab.coronabd.R;

import java.util.ArrayList;

public class MythsAdapter extends RecyclerView.Adapter<MythsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MythsItem> mythsItems;

    public MythsAdapter(Context mContext, ArrayList<MythsItem> mythsItems) {
        this.mContext = mContext;
        this.mythsItems = mythsItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_myths, parent, false);

        return new MythsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MythsItem nMythItems = mythsItems.get(position);

        View itemView = holder.itemView;
        ImageView mythsImage = holder.mythImage;
        View mythHolder =holder.mythHolder;

        Glide.with(itemView)
                .load(nMythItems.getImage())
                .into(mythsImage);

        mythHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(nMythItems.getImage()));
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mythsItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mythImage;
        View itemView;
        View mythHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            mythImage = itemView.findViewById(R.id.mythImage);
            mythHolder = itemView.findViewById(R.id.mythHolder);
        }
    }
}
