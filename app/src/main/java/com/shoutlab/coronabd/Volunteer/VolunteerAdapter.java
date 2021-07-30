package com.shoutlab.coronabd.Volunteer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shoutlab.coronabd.R;

import java.util.ArrayList;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VolunteerItems> volunteerItems;

    public VolunteerAdapter(Context mContext, ArrayList<VolunteerItems> volunteerItems) {
        this.mContext = mContext;
        this.volunteerItems = volunteerItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_donate, parent, false);

        return new VolunteerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VolunteerItems nVolunteerItems = volunteerItems.get(position);

        View itemView = holder.itemView;
        CardView donationCard = holder.donationCard;
        ImageView donationImage = holder.donationImage;
        TextView donationTitle = holder.donationTitle;

        donationTitle.setText(nVolunteerItems.getTitle());
        Glide.with(itemView)
                .load(nVolunteerItems.getLogo())
                .placeholder(R.drawable.loading_placeholder)
                .into(donationImage);

        donationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = nVolunteerItems.getUrl();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return volunteerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        CardView donationCard;
        ImageView donationImage;
        TextView donationTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            donationCard = itemView.findViewById(R.id.donationCard);
            donationImage = itemView.findViewById(R.id.donationImage);
            donationTitle = itemView.findViewById(R.id.donationTitle);
        }
    }
}
