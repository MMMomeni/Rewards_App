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

public class ProfileAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "ProfileAdapter";
    private List<Profile> profileList;
    private LeaderBoardActivity lBActivity;

    ProfileAdapter(List<Profile> sList, LeaderBoardActivity lBActivity) {
        this.profileList = sList;
        this.lBActivity = lBActivity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_card, parent, false);

        itemView.setOnClickListener(lBActivity); // means that main activity owns the onClickListener


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Profile s = profileList.get(position);
        int pointsAwarded = 0;

        int arraySize = s.getPointsAwarded().length();

        if (s.getPointsAwarded() != null && arraySize != 0){

            JSONArray ja = s.getPointsAwarded();

            for (int i = 0; i < arraySize; i++){
                try {
                    JSONObject jo = ja.getJSONObject(i);
                    pointsAwarded += Integer.parseInt(jo.getString("amount"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            holder.pointsLB.setText( String.format("%s", pointsAwarded));
        }
        else{
            holder.pointsLB.setText("0");
        }

        holder.fullNameLB.setText(String.format("%s, %s", s.getLastName(), s.getFirstName()));
        holder.depPosLB.setText(String.format("%s, %s", s.getDepartment(), s.getPosition()));

        if (s.getImageBytes() != null){
            byte[] imageBytes = Base64.decode(s.getImageBytes(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.profilePicture.setImageBitmap(bitmap);
        }





        //double percent means that we want a percent symbol in our output



    }


    @Override
    public int getItemCount() {
        return profileList.size();
    }
}
