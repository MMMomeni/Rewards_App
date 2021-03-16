package com.example.myapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView fullNameLB; // we can make these three public too, but we should never make them private
    TextView depPosLB;
    TextView pointsLB;
    ImageView profilePicture;


    MyViewHolder(View view){ //this objects will hold references to the items in our notes_list layout
        super(view);
        fullNameLB = view.findViewById(R.id.fullNameLB);
        depPosLB = view.findViewById(R.id.depPosLB);
        pointsLB = view.findViewById(R.id.pointsLB);
        profilePicture = view.findViewById(R.id.profilePicture5);

    }
}
