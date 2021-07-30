package com.shoutlab.coronabd.CoronaHospital;

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
import com.shoutlab.coronabd.R;
import java.util.ArrayList;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<HospitalItems> hospitalItems;

    public HospitalAdapter(Context mContext, ArrayList<HospitalItems> hospitalItems) {
        this.mContext = mContext;
        this.hospitalItems = hospitalItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_corona_hospital, parent, false);

        return new HospitalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HospitalItems nHospitalItems = hospitalItems.get(position);

        View hospitalHolder = holder.hospitalHolder;
        TextView hospitalName = holder.hospitalName;
        TextView hospitalAddress = holder.hospitalAddress;
        ImageView callHospital = holder.callHospital;

        hospitalName.setText(nHospitalItems.getName());
        hospitalAddress.setText(nHospitalItems.getAddress());

        if(position%2 == 0){
            hospitalHolder.setBackgroundColor(mContext.getResources().getColor(R.color.SS_faqBG));
        }

        if(nHospitalItems.getMobile().equals("")){
            callHospital.setVisibility(View.INVISIBLE);
        } else {
            callHospital.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + nHospitalItems.getMobile()));
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return hospitalItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View hospitalHolder;
        TextView hospitalName, hospitalAddress;
        ImageView callHospital;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalHolder = itemView.findViewById(R.id.hospitalHolder);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            hospitalAddress = itemView.findViewById(R.id.hospitalAddress);
            callHospital = itemView.findViewById(R.id.callHospital);
        }
    }

    public void filterList(ArrayList<HospitalItems> filteredList){
        hospitalItems = filteredList;
        notifyDataSetChanged();
    }
}
