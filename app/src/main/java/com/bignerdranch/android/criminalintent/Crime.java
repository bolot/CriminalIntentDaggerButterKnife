package com.bignerdranch.android.criminalintent;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";

    @SerializedName(JSON_ID)
    private UUID mId;
    @SerializedName(JSON_TITLE)
    private String mTitle;
    @SerializedName(JSON_DATE)
    private Date mDate;
    @SerializedName(JSON_SOLVED)
    private boolean mSolved;
    @SerializedName(JSON_PHOTO)
    private Photo mPhoto;
    @SerializedName(JSON_SUSPECT)
    private String mSuspect;
    
    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
    
    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo p) {
        mPhoto = p;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
}
