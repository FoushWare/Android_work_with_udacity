/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {
    /*********************************START GLOBALS************************************/

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    /** Adapter for the list of earthquakes */
    private EarthquakeAdapter mAdapter;
    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "http://earthquake.usgs.gov/fdsnws/event/1/query";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private TextView mEmptyStateTextView;
    private ProgressBar spinner;

    /*********************************END GLOBALS************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        /**************START Find References for the views in the XML files */
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        //find reference to the emptyTextView
        mEmptyStateTextView=(TextView) findViewById(R.id.empty_view);
         spinner=(ProgressBar) findViewById(R.id.loading_spinner);
        /***************END Find References for the views in the XML files *******/

        /* START set the EmptyStateTextViw when the adapter is Empty */
        earthquakeListView.setEmptyView(mEmptyStateTextView);
        /* END set the EmptyStateTextViw when the adapter is Empty */


        /** START check internet connectivity*/
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        boolean isConnected=activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
        /** END check internet connectivity*/

         /***********START LOG messages for debugging *********/
        Log.i(LOG_TAG," hello Mirna Here we trigure the initloader in oncreate Activity");
        /**END LOG messages for debugging *********/

// Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
// Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);



        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).

        //check if the is internet connection active the loader and if not set the empty_State textView to NO NETWORK
        if(isConnected==true){
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        }
        else {
            mEmptyStateTextView.setText(R.string.no_network);
            spinner.setVisibility(View.GONE);
        }






        /**************** START listener for itemClick*****************/
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Earthquake currentEarthquake =  mAdapter.getItem(i);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getmUrl());
                //create new intent to go from app to the browser
                Intent intent=new Intent(Intent.ACTION_VIEW,earthquakeUri);
                startActivity(intent);

            }
        });
        /*************** END listener for itemClick********************************/



    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG," hello Mirna Here i create new loader cause there is no previous one to reuse ");
        // Create a new loader for the given URL

        /**
         * we are using the sharedPreferences class to handle the reading and writing from preference Ui (xml/settings_main.xmal)
         *
         * */
        SharedPreferences sharedPrefs= PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude=sharedPrefs.getString(getString(R.string.settings_min_magnitude_key), String.valueOf(R.string.settings_min_magnitude_default));
        Uri baseUri=Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder=baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format","geojson");
        uriBuilder.appendQueryParameter("limit","10");
        uriBuilder.appendQueryParameter("minmag",minMagnitude);
        uriBuilder.appendQueryParameter("orderby","time");
        //this for reading the orderby from the user preference
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );




        return new EarthquakeLoader(this,uriBuilder.toString() );

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.i(LOG_TAG," hello Mirna Here i reset the loader after destroying the activity ");
        // Loader reset, so we can clear out our existing data.
               mAdapter.clear();
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> result) {
        /**START  stopping the spinner once the data is fetched**/
        spinner.setVisibility(View.GONE);
        /**END  stopping the spinner once the data is fetched**/

        Log.i(LOG_TAG," hello Mirna Here i will update the UI with the list of Earthquakes ");
        mEmptyStateTextView.setText(R.string.no_earthquake);
// Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (result != null && !result.isEmpty()) {
            mAdapter.addAll(result);
        }



    }
/**This is for the Fragment*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        //if the user click on the setting in the fragment we will sent him to new activity called setting_activity
        if(id==R.id.action_settings){
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
