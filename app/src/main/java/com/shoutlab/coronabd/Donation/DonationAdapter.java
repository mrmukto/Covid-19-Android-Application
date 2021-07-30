package com.shoutlab.coronabd.Donation;

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

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<DonationItems> donationItems;

    public DonationAdapter(Context mContext, ArrayList<DonationItems> donationItems) {
        this.mContext = mContext;
        this.donationItems = donationItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_donate, parent, false);

        return new DonationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonationItems nDonationItems = donationItems.get(position);

        View itemView = holder.itemView;
        CardView donationCard = holder.donationCard;
        ImageView donationImage = holder.donationImage;
        TextView donationTitle = holder.donationTitle;

        donationTitle.setText(nDonationItems.getTitle());
        Glide.with(itemView)
                .load(nDonationItems.getImage())
                .placeholder(R.drawable.loading_placeholder)
                .into(donationImage);

        donationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = nDonationItems.getUrl();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donationItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
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
