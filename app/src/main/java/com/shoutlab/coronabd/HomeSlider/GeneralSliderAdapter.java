package com.shoutlab.coronabd.HomeSlider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shoutlab.coronabd.R;

import java.util.ArrayList;

public class GeneralSliderAdapter extends RecyclerView.Adapter<GeneralSliderAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<SliderItems> sliderItems;

    public GeneralSliderAdapter(Context context, ArrayList<SliderItems> sliderItems) {
        mContext = context;
        this.sliderItems = sliderItems;
    }

    @NonNull
    @Override
    public GeneralSliderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_slider, parent, false);

        return new GeneralSliderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralSliderAdapter.ViewHolder holder, int position) {
        SliderItems nSliderItems = sliderItems.get(position);

        View itemView = holder.itemView;
        ImageView sliderImage = holder.sliderImage;
        CardView sliderCard = holder.sliderCard;

        if(position == 0){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sliderCard.getLayoutParams();
            params.setMargins(dpToPx(25),dpToPx(5),dpToPx(5),dpToPx(5));
            sliderCard.setLayoutParams(params);
        } else if(position == sliderItems.size()-1){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sliderCard.getLayoutParams();
            params.setMargins(dpToPx(5),dpToPx(5),dpToPx(25),dpToPx(5));
            sliderCard.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sliderCard.getLayoutParams();
            params.setMargins(dpToPx(5),dpToPx(5),dpToPx(5),dpToPx(5));
            sliderCard.setLayoutParams(params);
        }

//        sliderCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(nSliderItems.getImage()));
//                mContext.startActivity(browserIntent);
//            }
//        });

        Glide.with(itemView)
                .load(nSliderItems.getImage())
                .placeholder(R.drawable.slider_placeholder)
                .into(sliderImage);
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView sliderImage;
        CardView sliderCard;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            sliderCard = itemView.findViewById(R.id.slider_card);
            sliderImage = itemView.findViewById(R.id.sliderImage);
        }
    }

    private int dpToPx(int dp)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
