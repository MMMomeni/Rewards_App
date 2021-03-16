package com.example.myapplication;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;


public class Reward implements Serializable { //if we are going to pass this object over threads, we need to implement serializable
    //in order to be able to use Collection.sort() we have to implement comparable and the function to explain in case of sorting what need to
    //be compared to what
    private String giverName;
    private String amount;
    private String note;
    private String awardDate;


    Reward(String giverName, String amount, String note, String awardDate) {
        this.giverName = giverName;
        this.amount = amount;
        this.note = note;

        String[] result = awardDate.split("T");
        this.awardDate = result[0];

    }


    public String getGiverName() {
        return giverName;
    }

    public String getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getAwardDate() {
        return awardDate;
    }


}