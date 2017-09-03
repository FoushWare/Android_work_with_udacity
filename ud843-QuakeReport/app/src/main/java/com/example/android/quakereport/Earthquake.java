package com.example.android.quakereport;

/**
 * Created by tom on 31/01/17.
 */

public class Earthquake {
    //we will define some variables that will represent the earthquake information

    //magnitude of the earthquake
   private Double mMag;
    //place of the earthquake
    private String mplace;
    //Time  of the earthquake
    private long mTimeInMilliseconds;

       private String mUrl;





    //make constructor for the earthquake
    public Earthquake(Double mag,String place,long time,String url){
        mMag=mag;
        mplace=place;
        mTimeInMilliseconds= time;
        mUrl=url;
    }
    //Make a getter for every private variable
    public Double getmMag(){
        return mMag;
    }
    public String getMplace(){
        return mplace;
    }
    public long getmTimeInMilliseconds(){
        return mTimeInMilliseconds;
    }
    public String getmUrl(){
        return mUrl;
    }

}
