package com.bignerdranch.android.criminalintent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Photo implements Serializable { 
    private static final long serialVersionUID = 1L;

    private static final String JSON_FILENAME = "filename";

    @SerializedName(JSON_FILENAME)
    private String mFilename;

    /** create a new Photo with a generated filename */
    public Photo() {
    }

    /** create a Photo representing an existing file on disk */
    public Photo(String filename) {
        mFilename = filename;
    }

    public String getFilename() {
        return mFilename;
    }
}
