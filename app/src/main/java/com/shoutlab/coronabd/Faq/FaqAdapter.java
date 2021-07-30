package com.shoutlab.coronabd.Faq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shoutlab.coronabd.R;

import java.util.ArrayList;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<FaqItem> faqItems;

    public FaqAdapter(Context mContext, ArrayList<FaqItem> faqItems) {
        this.mContext = mContext;
        this.faqItems = faqItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_faq, parent, false);

        return new FaqAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FaqItem nFaqItem = faqItems.get(position);

        View faqHolder = holder.faqHolder;
        TextView question = holder.question;
        TextView answer = holder.answer;

        question.setText(nFaqItem.getQuestion());
        answer.setText(nFaqItem.getAnswer());

        if(position%2 == 0){
            faqHolder.setBackgroundColor(mContext.getResources().getColor(R.color.SS_faqBG));
        }
    }

    @Override
    public int getItemCount() {
        return faqItems.size();
    }

    public void filterList(ArrayList<FaqItem> filteredList){
        faqItems = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View faqHolder;
        TextView question, answer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            faqHolder = itemView.findViewById(R.id.faqHolder);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
        }
    }
}
