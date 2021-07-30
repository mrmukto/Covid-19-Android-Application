package com.shoutlab.coronabd.CovidArticle;

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

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ArticleItem> articleItems;

    public ArticleAdapter(Context mContext, ArrayList<ArticleItem> articleItems) {
        this.mContext = mContext;
        this.articleItems = articleItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_article, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticleItem nArticleItem = articleItems.get(position);

        View itemView = holder.itemView;
        View articleView = holder.articleView;
        ImageView articleImage = holder.articleImage;

        Glide.with(itemView)
                .load(nArticleItem.getImage())
                .placeholder(R.drawable.article_loading)
                .into(articleImage);

        articleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(nArticleItem.getUrl()));
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView, articleView;
        ImageView articleImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            articleView = itemView.findViewById(R.id.articleView);
            articleImage = itemView.findViewById(R.id.articleImage);
        }
    }

    public void filterList(ArrayList<ArticleItem> filteredList){
        articleItems = filteredList;
        notifyDataSetChanged();
    }

    private int dpToPx(int dp)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
