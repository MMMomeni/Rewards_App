package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements Serializable {

    private Profile profileInfo;
    private TextView fullName;
    private TextView username;
    private TextView location;
    private TextView pointsAwarded;
    private TextView remainingPoints;
    private TextView department;
    private TextView position;
    private TextView story;
    private ImageView profilePicture;
    private final List<String> loginInfo = new ArrayList<>();
    private String apiKey;
    private List<Reward> allRewards = new ArrayList<>();
    private RecyclerView recyclerView;
    private final String TITLEBAR = "Your Profile";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fullName = findViewById(R.id.fullNameBox);
        username = findViewById(R.id.usernamePasswordBox);
        location = findViewById(R.id.locationBox);
        remainingPoints = findViewById(R.id.pointsToAwardBox2);
        department = findViewById(R.id.departmentBox2);
        position = findViewById(R.id.positionBox2);
        story = findViewById(R.id.storyBox2);
        pointsAwarded = findViewById(R.id.pointsAwardedBox);
        profilePicture = findViewById(R.id.profileImage2);


        Intent intent = getIntent();
        profileInfo = (Profile) intent.getSerializableExtra("result");
        apiKey = intent.getStringExtra("apiKey");

        if (intent.hasExtra("allRewards")){
            allRewards.addAll( (List<Reward>) intent.getSerializableExtra("allRewards"));
        }


        profileHandler(profileInfo);

        updateRecycler();

    }

    private void profileHandler(Profile p){

            fullName.setText(p.getFirstName() + ", " + p.getLastName());
            username.setText("(" + p.getUserName() + ")");
            location.setText(p.getLocation());
            remainingPoints.setText(p.getRemainingPointsToAward());
            department.setText(p.getDepartment());
            position.setText(p.getPosition());
            story.setText(p.getStory());
            textToImage(p.getImageBytes());

            if (allRewards != null) {
                int allPointsAwarded = 0;
                for (int i = 0; i < allRewards.size(); i++) {
                    Reward reward = allRewards.get(i);
                    allPointsAwarded += Integer.parseInt(reward.getAmount());
                }

                pointsAwarded.setText(String.format("%s", allPointsAwarded));
            }
            else{
                pointsAwarded.setText("0");
            }

            loginInfo.add(apiKey);
            loginInfo.add(p.getUserName());
            loginInfo.add(p.getFirstName());
            loginInfo.add(p.getLastName());

            setTitle(TITLEBAR);

    }




    public void updateRecycler(){
        recyclerView = findViewById(R.id.recyclerRewards);
        RewardAdapter vh = new RewardAdapter(allRewards, this);
        recyclerView.setAdapter(vh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //updateTitleNoteCount();
    }

    public void textToImage(String v) {
        if (v == null) return;

        byte[] imageBytes = Base64.decode(v, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        profilePicture.setImageBitmap(bitmap);
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){ //this is the only code we have for menues
        //the menu we pass here is the actual menu we have made in layout
        //inflating means to build live objects
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.delete_menu:

                deleteDialog(null);

                return true;

            case R.id.edit_menu:

                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra("profileInfo", (Serializable) profileInfo);
                intent.putExtra("loginInfo", (Serializable) loginInfo);
                startActivity(intent);
                return true;

            case R.id.leaderBoard_menu:

                Intent intent2 = new Intent(this, LeaderBoardActivity.class);
                intent2.putExtra("loginInfo", (Serializable) loginInfo);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void deleteDialog(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("%s %s %s %s", "Delete Profile for", profileInfo.getFirstName(),
                profileInfo.getLastName(), "(The Reward app will sign out upon deletion)"));
        builder.setTitle("Delete Profile ?");
        builder.setIcon(R.drawable.logo);

        // Set the inflated view to be the builder's view

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               deleteProfileThread();


            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(ProfileActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();


            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    public void deleteProfileThread(){
        DeleteProfileRunnable DPThread = new  DeleteProfileRunnable(this, profileInfo.getUserName(), loginInfo);
        new Thread(DPThread).start();
    }

    public void deleteProfileResult(){
        Toast.makeText(ProfileActivity.this, "The Profile is deleted successfully", Toast.LENGTH_LONG).show();
        finish();
    }


}