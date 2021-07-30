package com.shoutlab.coronabd.Helpline;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shoutlab.coronabd.Faq.FaqItem;
import com.shoutlab.coronabd.R;

import java.util.ArrayList;

public class HelplineAdapter extends RecyclerView.Adapter<HelplineAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<HelplineItems> helplineItems;

    public HelplineAdapter(Context mContext, ArrayList<HelplineItems> helplineItems) {
        this.mContext = mContext;
        this.helplineItems = helplineItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_helpline, parent, false);

        return new HelplineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelplineItems nHelplineItems = helplineItems.get(position);

        TextView area = holder.area;
        TextView number = holder.number;
        View numberHolder = holder.numberHolder;
        ImageView callButton = holder.callButton;
        TextView details = holder.details;

        area.setText(nHelplineItems.getArea());
        number.setText(nHelplineItems.getNumber());
        details.setText(nHelplineItems.getDetails());

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + nHelplineItems.getNumber()));
                mContext.startActivity(intent);
            }
        });

        if(position%2 == 0){
            numberHolder.setBackgroundColor(mContext.getResources().getColor(R.color.SS_faqBG));
        }
    }

    @Override
    public int getItemCount() {
        return helplineItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View numberHolder;
        TextView area, number, details;
        ImageView callButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numberHolder = itemView.findViewById(R.id.numberHolder);
            area = itemView.findViewById(R.id.area);
            number = itemView.findViewById(R.id.mobileNumber);
            details = itemView.findViewById(R.id.details);
            callButton = itemView.findViewById(R.id.callButton);
        }
    }

    public void filterList(ArrayList<HelplineItems> filteredList){
        helplineItems = filteredList;
        notifyDataSetChanged();
    }
}
