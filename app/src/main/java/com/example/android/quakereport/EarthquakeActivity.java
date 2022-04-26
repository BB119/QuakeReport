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

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    //Adapter for the list of earthquakes
    private EarthquakeAdapter mAdapter;
    private ListView earthquakeListView;
    private TextView mEemptyTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = (ListView) findViewById(R.id.list);


        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new EarthquakeAdapter(this, new ArrayList<>());

        // Setting the onClick listener
        earthquakeListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String url = mAdapter.getItem(position).getURL();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        mProgressBar = findViewById(R.id.progress_bar);
        mEemptyTextView = findViewById(R.id.empty_text);
        earthquakeListView.setEmptyView(mEemptyTextView);

        // Checking if the device is connected and acting accordingly
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            // Updating th UI using a ViewModel Object
            EarthquakeViewModel earthquakesModel = ViewModelProviders.of(this).get(EarthquakeViewModel.class);
            earthquakesModel.getEarthquakes().observe(this, earthquakes -> {
                // Make the progress bar go away
                mProgressBar.setVisibility(View.GONE);
                // update UI
                updateUI(earthquakes);
                mEemptyTextView.setText("No earthquakes found");
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEemptyTextView.setText("No internet connection");
        }


    }



    private void updateUI(List<EarthquakeEvent> earthquakesArray){
        mAdapter.clear();

        if (earthquakesArray == null || earthquakesArray.isEmpty()){
            return;
        }

        mAdapter.addAll(earthquakesArray);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
