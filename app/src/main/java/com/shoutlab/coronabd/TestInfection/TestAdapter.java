package com.shoutlab.coronabd.TestInfection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.shoutlab.coronabd.R;
import java.util.ArrayList;
import static com.shoutlab.coronabd.TestInfectionActivity.answerList;
import static com.shoutlab.coronabd.TestInfectionActivity.progressBar;
import static com.shoutlab.coronabd.TestInfectionActivity.progressCount;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<TestItems> testItems;

    public TestAdapter(Context mContext, ArrayList<TestItems> testItems) {
        this.mContext = mContext;
        this.testItems = testItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.recycle_test, parent, false);

        return new TestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestItems nTestItems = testItems.get(position);

        View itemView = holder.itemView;
        TextView question = holder.question;
        ImageView quesImage = holder.quesImage;
        ImageView noButton = holder.noButton;
        ImageView yesButton = holder.yesButton;
        CardView quesCard = holder.quesCard;

        if(position == 0){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) quesCard.getLayoutParams();
            params.setMargins(dpToPx(35),dpToPx(10),dpToPx(10),dpToPx(10));
            quesCard.setLayoutParams(params);
        } else if(position == testItems.size()-1){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) quesCard.getLayoutParams();
            params.setMargins(dpToPx(10),dpToPx(10),dpToPx(35),dpToPx(10));
            quesCard.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) quesCard.getLayoutParams();
            params.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
            quesCard.setLayoutParams(params);
        }

        question.setText(nTestItems.getQuestion());

        Glide.with(itemView)
                .load(nTestItems.getImage())
                .placeholder(R.drawable.test_infection)
                .into(quesImage);

        if(progressCount.contains(nTestItems.getId())){
            if(answerList.contains(nTestItems.getId())){
                yesButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_enabled));
                noButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cancel_disabled));
            } else {
                yesButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_disabled));
                noButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cancel_enabled));
            }
        } else {
            yesButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_disabled));
            noButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cancel_disabled));
        }

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_enabled));
                noButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cancel_disabled));
                if(!answerList.contains(nTestItems.getId())){
                    answerList.add(nTestItems.getId());
                }

                if(!progressCount.contains(nTestItems.getId())){
                    progressCount.add(nTestItems.getId());
                    progressBar.setProgress(progressCount.size());
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_disabled));
                noButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cancel_enabled));
                answerList.remove(nTestItems.getId());

                if(!progressCount.contains(nTestItems.getId())){
                    progressCount.add(nTestItems.getId());
                    progressBar.setProgress(progressCount.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return testItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView question;
        CardView quesCard;
        ImageView noButton, yesButton, quesImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            question = itemView.findViewById(R.id.question);
            quesImage = itemView.findViewById(R.id.quesImage);
            noButton = itemView.findViewById(R.id.noButton);
            yesButton = itemView.findViewById(R.id.yesButton);
            quesCard = itemView.findViewById(R.id.quesCard);
        }
    }

    private int dpToPx(int dp)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
