package com.example.myapplication;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Serializable {

    private String fName;
    private String lName;
    private String sID;
    private String email;
    private String apiKey = "";
    private TextView username;
    private TextView password;
    private MyProjectSharedPreference myPrefs;
    private CheckBox saveCheckBox;
    private String blean;


    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;

    //private static String locationString = "Unspecified Location";
    private String deviceLocation = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myPrefs = new MyProjectSharedPreference(this);
        apiKey = myPrefs.getValue(getString(R.string.apiKey));
        blean = myPrefs.getValue(getString(R.string.checked));

        //loginInfo.setApiKey(apiKey);
        //Toast.makeText(this, loginInfo.getLoginApiKey(), Toast.LENGTH_SHORT).show();
        username = findViewById(R.id.usernameField);
        password = findViewById(R.id.passwordField);
        saveCheckBox = findViewById(R.id.credentialSaveCheckBox);


        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        determineLocation();





        if (blean.equals("true")) {
            saveCheckBox.setChecked(true);
            username.setText(myPrefs.getValue(getString(R.string.loginUsername)));
            password.setText(myPrefs.getValue(getString(R.string.loginPassword)));
        }
        else{
            saveCheckBox.setChecked(false);
        }

        if (apiKey.equals("")){
            infoDialog(null);
        }
    }


/*
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        if(checked){
            myPrefs.save(getString(R.string.loginUsername), username.getText().toString());
            myPrefs.save(getString(R.string.loginPassword), password.getText().toString());
        }
        else {
            myPrefs.removeValue(getString(R.string.loginUsername));
            myPrefs.removeValue(getString(R.string.loginPassword));
        }


            // TODO: Veggie sandwich
        }

 */






    public void deleteSavedAPI(View v){
        myPrefs.removeValue(getString(R.string.apiKey));
        myPrefs.removeValue(getString(R.string.loginUsername));
        myPrefs.removeValue(getString(R.string.loginPassword));

        //loginInfo.setApiKey(null);
        Toast.makeText(MainActivity.this, "Saved API key is deleted", Toast.LENGTH_LONG).show();
        infoDialog(null);
    }

    public void loginButton(View v){
        if (saveCheckBox.isChecked()) {
            myPrefs.save(getString(R.string.loginUsername), username.getText().toString());
            myPrefs.save(getString(R.string.loginPassword), password.getText().toString());
            blean = "true";
            myPrefs.save(getString(R.string.checked), blean);
        }
        else {
            myPrefs.removeValue(getString(R.string.loginUsername));
            myPrefs.removeValue(getString(R.string.loginPassword));
            blean = "false";
            myPrefs.save(getString(R.string.checked), blean);
        }

        LoginAPIRunnable LAR = new LoginAPIRunnable(this, username.getText().toString(),
                password.getText().toString(), apiKey);
        new Thread(LAR).start();
    }


    public void infoDialog(View v) {
        // Dialog with a layout

        // Inflate the dialog's layout

            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.dialog1, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need to request an API key:");
            builder.setTitle("API key Needed");
            builder.setIcon(R.drawable.logo);

            // Set the inflated view to be the builder's view
            builder.setView(view);


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    EditText et1 = view.findViewById(R.id.dialogFirstName);
                    fName = et1.getText().toString();
                    EditText et2 = view.findViewById(R.id.dialogLastName);
                    lName = et2.getText().toString();
                    EditText et3 = view.findViewById(R.id.dialogStudentEmail);
                    email = et3.getText().toString();
                    EditText et4 = view.findViewById(R.id.dialogStudentID);
                    sID = et4.getText().toString();

                    String url = "http://christopherhield.org/api/Profile/GetStudentApiKey?firstName=" + et1.getText().toString()
                            + "&lastName=" + et2.getText().toString() + "&studentId=" + et4.getText().toString() +
                            "&email=" + et3.getText().toString();

                    if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || sID.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Error! Please Fill All The Fields", Toast.LENGTH_LONG).show();
                        infoDialog(null);
                    }
                    else if (!email.contains(".edu")){
                        Toast.makeText(MainActivity.this, "Error! Incorrect Student Email!", Toast.LENGTH_LONG).show();
                        infoDialog(null);
                    }
                    else
                        apiCall(url);
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();


                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

    }

    public void apiCall (String n){
        GetAPIRunnable getAPIRunnable = new GetAPIRunnable(this, n);
        new Thread(getAPIRunnable).start();
    }


    public void resultDialog(String v) {
        // Simple dialog - no buttons.
        apiKey = v;
        myPrefs.save(getString(R.string.apiKey), apiKey);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.logo);

        builder.setMessage("Name: " + fName + " " + lName + "\n" +
                "StudentID: " + sID + "\n" + "Email: " + email + "\n" + "API key: " + apiKey);
        builder.setTitle("API Key Received and Stored");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void createNewProfile(View v){
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra("apiKey", apiKey);
        intent.putExtra("location", deviceLocation);
        startActivityForResult(intent, 1);
    }

    public void loginThreadResult(Profile result, List<Reward> allRewards){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("result", (Serializable) result);
        intent.putExtra("apiKey", apiKey);
        intent.putExtra("allRewards", (Serializable) allRewards);
        startActivityForResult(intent, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

/*
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("fName", fName);
        outState.putString("lName", lName);
        outState.putString("sID", sID);
        outState.putString("email", email);
        outState.putString("apiKey", apiKey);

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        fName = savedInstanceState.getString("fName");
        lName = savedInstanceState.getString("lName");
        sID = savedInstanceState.getString("sID");
        email = savedInstanceState.getString("email");
        apiKey = savedInstanceState.getString("apiKey");
    }

 */






    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            deviceLocation = getPlace(location);

                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }


    private String getPlace(Location loc) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            return String.format("%s, %s", city, state);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    Toast.makeText(this, "Location Permissin Denied!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}