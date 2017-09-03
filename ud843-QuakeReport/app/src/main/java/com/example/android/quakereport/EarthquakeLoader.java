package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.util.List;

/**
 * Created by tom on 06/02/17.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    /** Query URL */
    private String mUrl;

    public EarthquakeLoader(Context context,String url) {
        super(context);
        mUrl=url;
    }



    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public  List<Earthquake> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Earthquake> earthquakes = null;
        try {
            earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return earthquakes;

    }



}
