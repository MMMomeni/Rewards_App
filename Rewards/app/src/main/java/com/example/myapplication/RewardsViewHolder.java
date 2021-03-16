package com.example.myapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RewardsViewHolder extends RecyclerView.ViewHolder {
    TextView giverName; // we can make these three public too, but we should never make them private
    TextView amount;
    TextView note;
    TextView awardDate;


    RewardsViewHolder(View view){ //this objects will hold references to the items in our notes_list layout
        super(view);
        giverName = view.findViewById(R.id.fullName_profile_recaycler);
        amount = view.findViewById(R.id.amount_profile_recycler);
        note = view.findViewById(R.id.comment_profile_recycler);
        awardDate = view.findViewById(R.id.date);

    }
}
