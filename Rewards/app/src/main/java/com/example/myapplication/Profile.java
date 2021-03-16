package com.example.myapplication;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;



public class Profile implements Serializable, Comparable<Profile> { //if we are going to pass this object over threads, we need to implement serializable
    //in order to be able to use Collection.sort() we have to implement comparable and the function to explain in case of sorting what need to
    //be compared to what
    private String firstName;
    private String lastName;
    private String userName;
    private String department;
    private String story;
    private String position;
    private String password;
    private String remainingPointsToAward;
    private String location;
    private String imageBytes;
    private JSONArray pointsAwarded;
    private String totalPointsAwarded;


    Profile(String firstName, String lastName, String userName, String department, String story, String position,
            String password, String remainingPointsToAward, String location, String imageBytes, JSONArray pointsAwarded, String points) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.remainingPointsToAward = remainingPointsToAward;
        this.location = location;
        this.imageBytes = imageBytes;
        this.pointsAwarded = pointsAwarded;
        this.totalPointsAwarded = points;


    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getDepartment() {
        return department;
    }

    public String getStory() {
        return story;
    }

    public String getPosition() {
        return position;
    }

    public String getPassword() {
        return password;
    }

    public String getRemainingPointsToAward() {
        return remainingPointsToAward;
    }

    public String getLocation() {
        return location;
    }

    public String getImageBytes() {
        return imageBytes;
    }

    public JSONArray getPointsAwarded() {
        return pointsAwarded;
    }

    public String getTotalPointsAwarded() {
        return totalPointsAwarded;
    }

    @Override
    public int compareTo(Profile s) {
        return totalPointsAwarded.compareTo(s.getTotalPointsAwarded());
    }
}
