package com.example.android.quakereport;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeViewModel extends AndroidViewModel {
    private static final String QUERY_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";


    private MutableLiveData<List<EarthquakeEvent>> earthquakes;

    public EarthquakeViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<EarthquakeEvent>> getEarthquakes() {
        if (earthquakes == null) {
            earthquakes = new MutableLiveData<>();
            loadEarthquakes();
        }
        return earthquakes;
    }

    private void loadEarthquakes() {
        EarthquakesAsyncTask earthquakesAsyncTask = new EarthquakesAsyncTask();

        // Getting a shared preferences instance
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication().getApplicationContext());

        // Getting the minimum magnitude from shared preferences
        String minMagnitude = sharedPreferences.getString(getApplication().getString(R.string.settings_min_magnitude_key),
                getApplication().getString(R.string.settings_min_magnitude_default));

        // Getting the order_by parameter value from shared preferences
        String orderBy = sharedPreferences.getString(getApplication().getString(R.string.settings_order_by_key),
                getApplication().getString(R.string.settings_order_by_default));

        // Getting the base URI and preparing it for query formatting
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "20");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);


        earthquakesAsyncTask.execute(uriBuilder.toString());
    }

    private class EarthquakesAsyncTask extends AsyncTask<String, Void, List<EarthquakeEvent>> {
        @Override
        protected List<EarthquakeEvent> doInBackground(String... strings) {
            if (strings.length < 1 || strings[0].isEmpty()){
                return null;
            }
            return QueryUtils.fetchEarthquakeEvents(strings[0]);
        }

        @Override
        protected void onPostExecute(List<EarthquakeEvent> earthquakeEvents) {
            earthquakes.setValue(earthquakeEvents);
        }
    }
}
