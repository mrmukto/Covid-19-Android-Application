package com.shoutlab.coronabd.Situation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shoutlab.coronabd.R;

import java.util.ArrayList;

public class SituationAdapter extends RecyclerView.Adapter<SituationAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<SituationItems> situationItems;

    public SituationAdapter(Context mContext, ArrayList<SituationItems> situationItems) {
        this.mContext = mContext;
        this.situationItems = situationItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_situation, parent, false);

        return new SituationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SituationItems nSituationItems = situationItems.get(position);

        View situationHolder = holder.situationHolder;
        TextView countryName = holder.countryName;
        TextView countryCases = holder.countryCases;

        countryName.setText(nSituationItems.getCountryName());
        countryCases.setText(nSituationItems.getCountryCases());

        if(position%2 == 0){
            situationHolder.setBackgroundColor(mContext.getResources().getColor(R.color.SS_Grey));
        }
    }

    @Override
    public int getItemCount() {
        return situationItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View situationHolder;
        TextView countryName, countryCases;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            situationHolder = itemView.findViewById(R.id.situationHolder);
            countryName = itemView.findViewById(R.id.countryName);
            countryCases = itemView.findViewById(R.id.countryCases);
        }
    }
}
