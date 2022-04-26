package com.example.android.quakereport;

public class EarthquakeEvent {
    // Properties
    double mMagnitude;
    String mPlace;
    long mDate;
    String mURL;

    public EarthquakeEvent(double mag, String place, long date, String url) {
        mMagnitude = mag;
        mPlace = place;
        mDate = date;
        mURL = url;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public String getPlace() {
        return mPlace;
    }

    public long getDate() {
        return mDate;
    }

    public String getURL() {
        return mURL;
    }
}
