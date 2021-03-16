package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardsViewHolder> {

    private static final String TAG = "RewardAdapter";
    private List<Reward> allRewards;
    private ProfileActivity profileActivity;

    RewardAdapter(List<Reward> sList, ProfileActivity profileActivity) {
        this.allRewards = sList;
        this.profileActivity = profileActivity;
    }

    @NonNull
    @Override
    public RewardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_rewards_card, parent, false);

        //itemView.setOnClickListener(lBActivity); // means that main activity owns the onClickListener


        return new RewardsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardsViewHolder holder, int position) {
        Reward s = allRewards.get(position);

        holder.giverName.setText(s.getGiverName());
        holder.amount.setText(s.getAmount());
        holder.note.setText(s.getNote());
        holder.awardDate.setText(s.getAwardDate());

    }

    @Override
    public int getItemCount() {
        return allRewards.size();
    }
}


