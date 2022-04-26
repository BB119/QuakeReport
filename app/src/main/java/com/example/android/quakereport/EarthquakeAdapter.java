package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeAdapter extends ArrayAdapter<EarthquakeEvent> {
    // Properties

    /**
     * Default EarthquakeAdapter constructor
     *
     * @param context the context of the activity
     * @param objects the ArrayList of the earthquakes
     */
    public EarthquakeAdapter(Context context, ArrayList<EarthquakeEvent> objects) {
        super(context, 0, objects);
    }

    /**
     * Gets the view for each row in the list view
     *
     * @param position    the position of the earthquake to display in the array data
     * @param convertView the recycled or the current view for the row
     * @param parent      the parent view group of the row
     * @return the current row view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View currentView = convertView;
        EarthquakeEvent currentEarthquake = getItem(position);

        if (currentView == null) {
            currentView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView magnitudeTextView = currentView.findViewById(R.id.magnitude_text_view);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        DecimalFormat decimalFormatter = new DecimalFormat("#.#");
        magnitudeTextView.setText(decimalFormatter.format(currentEarthquake.getMagnitude()));

        TextView primaryLocationTextView = currentView.findViewById(R.id.primary_location_text_view);
        TextView locationOffsetTextView = currentView.findViewById(R.id.location_offset_text_view);
        String primaryLocation = currentEarthquake.getPlace();
        String locationOffset;
        if (primaryLocation.contains(" of ")) {
            int startingIndex = primaryLocation.indexOf(" of ") + 3;
            locationOffset = primaryLocation.substring(0, startingIndex);
            primaryLocation = primaryLocation.substring(startingIndex + 1);
        } else {
            locationOffset = "Near the";
        }

        primaryLocationTextView.setText(primaryLocation);
        locationOffsetTextView.setText(locationOffset);

        TextView dateTextView = currentView.findViewById(R.id.date_text_view);
        // Formatting the date
        Date dateObject = new Date(currentEarthquake.getDate());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy");
        String dateToDisplay = dateFormatter.format(dateObject);

        dateTextView.setText(dateToDisplay);

        TextView timeTextView = currentView.findViewById(R.id.time_text_view);
        // Formatting the time
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm a");
        String timeToDisplay = timeFormatter.format(dateObject);

        timeTextView.setText(timeToDisplay);

        return currentView;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorId;
        int roundedMagnitude = (int) Math.floor(magnitude);

        switch (roundedMagnitude) {
            case 0:
            case 1:
                magnitudeColorId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorId = R.color.magnitude9;
                break;
            default:
                magnitudeColorId = R.color.magnitude10plus;
                break;

        }

        return ContextCompat.getColor(getContext(), magnitudeColorId);
    }
}
