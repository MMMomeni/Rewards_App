package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RewardActivity extends AppCompatActivity {

    private TextView fullName;
    private TextView pointsAwarded;
    private TextView department;
    private TextView position;
    private TextView story;
    private EditText rewardPointsToSend;
    private EditText comment;
    private ImageView profilePicture;
    private Intent intent;
    private List<String> loginInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        fullName = findViewById(R.id.fullNameReward);
        pointsAwarded = findViewById(R.id.pointsAwardedReward);
        department = findViewById(R.id.departmentReward);
        position = findViewById(R.id.positionReward);
        story = findViewById(R.id.storyReward);
        rewardPointsToSend = findViewById(R.id.pointsToSendReward);
        comment = findViewById(R.id.commentBoxReward);
        profilePicture = findViewById(R.id.imageView3);


        intent = getIntent();
        profileHandler(intent);

        setTitle(intent.getStringExtra("firstName") + " " + intent.getStringExtra("lastName"));


    }

    private void profileHandler(Intent intent){

        fullName.setText(String.format("%s, %s", intent.getStringExtra("firstName"), intent.getStringExtra("lastName")));
        pointsAwarded.setText(String.format("%s", intent.getIntExtra("awardPoints",0)));
        department.setText(intent.getStringExtra("department"));
        position.setText(intent.getStringExtra("position"));
        story.setText(intent.getStringExtra("story"));


        if (intent.getStringExtra("profilePic") != null){
            byte[] imageBytes = Base64.decode(intent.getStringExtra("profilePic"), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profilePicture.setImageBitmap(bitmap);
        }

        loginInfo.addAll((List<String>) intent.getSerializableExtra("loginInfo"));

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){ //this is the only code we have for menues
        //the menu we pass here is the actual menu we have made in layout
        //inflating means to build live objects
        getMenuInflater().inflate(R.menu.reward_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.save_menu_reward:
                if (comment.getText().toString().isEmpty() || rewardPointsToSend.getText().toString().isEmpty()){
                    Toast.makeText(this, "You need to fill all the fields!", Toast.LENGTH_LONG).show();
                    return false;
                }

                addRewardsPoints(null);


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void addRewardsPoints(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("%s %s %s %s", "Add rewards for", intent.getStringExtra("firstName"),
                intent.getStringExtra("lastName"), "?"));
        builder.setTitle("Add Rewards Points ?");
        builder.setIcon(R.drawable.logo);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addRewardsPointsThread();

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(RewardActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addRewardsPointsThread(){
        List<String> infoList = new ArrayList<>();
        infoList.add(intent.getStringExtra("username"));
        infoList.add(rewardPointsToSend.getText().toString());
        infoList.add(comment.getText().toString());

        RewardAPIRunnable RAR = new  RewardAPIRunnable(this, infoList, loginInfo);
        new Thread(RAR).start();
    }

    public void rewardThreadHandler(){
        Toast.makeText(this, "Points Sent Successfully", Toast.LENGTH_LONG).show();
        finish();
    }




}