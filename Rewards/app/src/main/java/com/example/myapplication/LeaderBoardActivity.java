package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    private final List<Profile> profileList = new ArrayList<>();
    private RecyclerView recyclerView;
    private List<String> loginInfo = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        Intent intent = getIntent();
        loginInfo.addAll((List<String>) intent.getSerializableExtra("loginInfo"));

        AllProfilesRunnable APR = new AllProfilesRunnable(this, loginInfo);
        new Thread(APR).start();
/*
        for (int i = 0; i< 12; i++){
            profileList.add(new Profile("a", "b", "c", "d",
                    "e", "f", "g", "h", "i", "j", null));
        }
        */
        updateRecycler();

        setTitle("Leaderboard");

    }


    public void threadResult(List<Profile> proList) {
        profileList.clear();
        profileList.addAll(proList);
        Collections.sort(profileList);
        Collections.reverse(profileList);
        updateRecycler();
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Profile m = profileList.get(pos);

        if (!m.getUserName().equals(loginInfo.get(1))){
            int totalAwards = awardsCalculator(m);

            String username = m.getUserName();
            String firstName = m.getFirstName();
            String lastName = m.getLastName();
            String department = m.getDepartment();
            String position = m.getPosition();
            String Story = m.getStory();
            String profilePic = m.getImageBytes();

            Intent data = new Intent(this , RewardActivity.class); // this is explicit intent

            data.putExtra("username", username);
            data.putExtra("firstName", firstName);
            data.putExtra("lastName", lastName);
            data.putExtra("department", department);
            data.putExtra("position", position);
            data.putExtra("Story", Story);
            data.putExtra("profilePic", profilePic);
            data.putExtra("awardPoints", totalAwards);
            data.putExtra("loginInfo", (Serializable) loginInfo);
            startActivityForResult(data, 30);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 30){
            AllProfilesRunnable APR = new AllProfilesRunnable(this, loginInfo);
            new Thread(APR).start();
        }

    }



    public int awardsCalculator(Profile p){
        int allPointsAwarded = 0;

        int arraySize = p.getPointsAwarded().length();

        if (p.getPointsAwarded() != null && arraySize != 0){

            JSONArray ja = p.getPointsAwarded();

            for (int i = 0; i < arraySize; i++){
                try {
                    JSONObject jo = ja.getJSONObject(i);
                    allPointsAwarded += Integer.parseInt(jo.getString("amount"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return allPointsAwarded;
        }
        else{
            return 0;
        }
    }



    public void updateRecycler(){
        recyclerView = findViewById(R.id.recyclerLB);
        ProfileAdapter vh = new ProfileAdapter(profileList, this);
        recyclerView.setAdapter(vh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //updateTitleNoteCount();
    }

}