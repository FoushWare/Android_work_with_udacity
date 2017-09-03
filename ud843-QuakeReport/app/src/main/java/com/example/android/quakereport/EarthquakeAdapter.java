package com.example.android.quakereport;

import android.app.Activity;
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

/**
 * Created by tom on 31/01/17.
 */



public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    //the constructor
    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> Earthquake) {
        super(context, 0, Earthquake);
    }

    //make variables for primary_place and offset_place
    String primaryPlace;
    String offsetPlace;
    //make variable for the seperator
    private static final String LOCATION_SEPERATOR="of";

    //convetView is the old view to use you should check if it's not null before use  if it's null you should inflate the layout
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //check if exitsting view is being reused , otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Earthquake currentEarthquake=getItem(position);
        TextView mag=(TextView)listItemView.findViewById(R.id.mag);
        //this part for magnitude
        DecimalFormat decimalFormater=new DecimalFormat("0.0");
        String decimalMag=decimalFormater.format(currentEarthquake.getmMag());

        mag.setText(decimalMag);
        //color for the magnitude

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable
        GradientDrawable magnitudeCircle=(GradientDrawable)mag.getBackground();
        int magnitudeColor= getMagnitudeColor(currentEarthquake.getmMag());
        magnitudeCircle.setColor(magnitudeColor);




        //This part is for place
TextView primary=(TextView)listItemView.findViewById(R.id.primary_place);
TextView offset=(TextView)listItemView.findViewById(R.id.offset_place);

        if(currentEarthquake.getMplace().contains(LOCATION_SEPERATOR)){
            String []parts=currentEarthquake.getMplace().split(LOCATION_SEPERATOR);
            offsetPlace=parts[0];
            primaryPlace=parts[1];
        }else {
            offsetPlace=getContext().getString(R.string.near_the);
            primaryPlace=currentEarthquake.getMplace();

        }


        primary.setText(primaryPlace);
        offset.setText(offsetPlace);

        //This part is for date and time
TextView date=(TextView)listItemView.findViewById(R.id.date);
        Date dateObject = new Date(currentEarthquake.getmTimeInMilliseconds());
        date.setText(formatDate(dateObject));
        TextView time=(TextView)listItemView.findViewById(R.id.time);
        time.setText(formatTime(dateObject));


        return listItemView;
    }


    /*
    * Helper methods
    * */

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    //helper Method for choose the correct color for earthquake 's value
    private int getMagnitudeColor(Double mag){
     int magnitudeColorResourceId;
        int magnitudeFloor=(int)Math.floor(mag);
        switch (magnitudeFloor){
            case 0:
            case 1:
                magnitudeColorResourceId=R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId=R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId=R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId=R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId=R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId=R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId=R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId=R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId=R.color.magnitude9;
                break;
                default:
                    magnitudeColorResourceId=R.color.magnitude10plus;
                    break;

        }



        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

}
