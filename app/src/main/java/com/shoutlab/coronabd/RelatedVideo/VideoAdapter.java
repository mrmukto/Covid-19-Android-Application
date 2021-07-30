package com.shoutlab.coronabd.RelatedVideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shoutlab.coronabd.R;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VideoItems> videoItems;

    public VideoAdapter(Context mContext, ArrayList<VideoItems> videoItems) {
        this.mContext = mContext;
        this.videoItems = videoItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_related_video, parent, false);

        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoItems nVideoItems = videoItems.get(position);

        float radius = 15f;

        View playButton2 = holder.playButton2;
        View playButton3 = holder.playButton3;
        View itemView = holder.itemView;
        Button playButton4 = holder.playButton4;
        TextView videoTitle = holder.videoTitle;
        TextView videoDescription = holder.videoDescription;
        ImageView videoThumbnail = holder.videoThumbnail;
        BlurView playButton = holder.playButton;
        ViewGroup rootView = holder.rootView;
        Drawable windowBackground = ((Activity) mContext).getWindow().getDecorView().getBackground();
        CardView videoCard = holder.videoCard;

        if(position == 0){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) videoCard.getLayoutParams();
            params.setMargins(dpToPx(25),dpToPx(5),dpToPx(5),dpToPx(5));
            videoCard.setLayoutParams(params);
        } else if(position == videoItems.size()-1){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) videoCard.getLayoutParams();
            params.setMargins(dpToPx(5),dpToPx(5),dpToPx(25),dpToPx(5));
            videoCard.setLayoutParams(params);
        }

        playButton.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(mContext))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playButton.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            playButton.setClipToOutline(true);
        }

        videoTitle.setText(nVideoItems.getTitle());
        videoDescription.setText(nVideoItems.getDescription());

        Glide.with(itemView)
                .load(nVideoItems.getThumbnail())
                .placeholder(R.drawable.video_thumbnail)
                .into(videoThumbnail);

        videoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(nVideoItems.getUrl()));
                intent.setPackage("com.google.android.youtube");
                mContext.startActivity(intent);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(nVideoItems.getUrl()));
                intent.setPackage("com.google.android.youtube");
                mContext.startActivity(intent);
            }
        });

        playButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(nVideoItems.getUrl()));
                intent.setPackage("com.google.android.youtube");
                mContext.startActivity(intent);
            }
        });

        playButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(nVideoItems.getUrl()));
                intent.setPackage("com.google.android.youtube");
                mContext.startActivity(intent);
            }
        });

        playButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(nVideoItems.getUrl()));
                intent.setPackage("com.google.android.youtube");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        View playButton2;
        View playButton3;
        Button playButton4;
        BlurView playButton;
        ViewGroup rootView;
        CardView videoCard;
        TextView videoTitle, videoDescription;
        ImageView videoThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            rootView = itemView.findViewById(R.id.videoRoot);
            playButton = itemView.findViewById(R.id.playButtonBlur);
            videoCard = itemView.findViewById(R.id.videoCard);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoDescription = itemView.findViewById(R.id.videoDescription);
            videoThumbnail = itemView.findViewById(R.id.sliderImage);
            playButton2 = itemView.findViewById(R.id.videoCard2);
            playButton3 = itemView.findViewById(R.id.playButton3);
            playButton4 = itemView.findViewById(R.id.playButton4);
        }
    }

    private int dpToPx(int dp)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
