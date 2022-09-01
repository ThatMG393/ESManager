package com.thatmg393.esmanager.data;

public class FirebaseUpload {
    public String mFileName;
    public String mLogUrl;

    public FirebaseUpload() {}

    public FirebaseUpload(String fileName, String logUrl) {
        if (fileName.trim().equals("")) {
            fileName = "No Name";
        }

        mFileName = fileName;
        mLogUrl = logUrl;
    }

    public String getName() {
        return mFileName;
    }

    public void setName(String fileName) {
        mFileName = fileName;
    }
    
    public String getImageUrl() {
        return mLogUrl;
    }

    public void setImageUrl(String logUrl) {
        mLogUrl = logUrl;
    }
}
