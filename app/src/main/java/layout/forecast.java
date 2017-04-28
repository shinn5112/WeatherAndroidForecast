package layout;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.weatheraf.weatherandroidforecast.MainActivity;
import net.weatheraf.weatherandroidforecast.R;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import WeatherAPI.DailyDataPoint;
import WeatherAPI.DataPoint;
import WeatherAPI.WeatherData;

/**
 * A simple {@link Fragment} subclass.
 */
public class forecast extends Fragment{

    private TextView[][] dailyViews = new TextView[7][]; // 2d array of text views for the daily forecast.
    private ImageView[] dailyImages = new ImageView[7];
    private ImageView[] dailyIcons = new ImageView[7];
    private ImageView background;
    private WeatherData weatherData;
    private TextView dailyField;

    public forecast() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        //setup
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        background = (ImageView) view.findViewById(R.id.forecastBackground);
        dailyField = (TextView)view.findViewById(R.id.DailyField);

        // setting up daily forecast stuff
        dailyViews[0] = new TextView[]{(TextView)view.findViewById(R.id.time0), (TextView)view.findViewById(R.id.summary0), (TextView)view.findViewById(R.id.hiLow0), (TextView)view.findViewById(R.id.precipitation0)};
        dailyViews[1] = new TextView[]{(TextView)view.findViewById(R.id.time1), (TextView)view.findViewById(R.id.summary1), (TextView)view.findViewById(R.id.hiLow1), (TextView)view.findViewById(R.id.precipitation1)};
        dailyViews[2] = new TextView[]{(TextView)view.findViewById(R.id.time2), (TextView)view.findViewById(R.id.summary2), (TextView)view.findViewById(R.id.hiLow2), (TextView)view.findViewById(R.id.precipitation2)};
        dailyViews[3] = new TextView[]{(TextView)view.findViewById(R.id.time3), (TextView)view.findViewById(R.id.summary3), (TextView)view.findViewById(R.id.hiLow3), (TextView)view.findViewById(R.id.precipitation3)};
        dailyViews[4] = new TextView[]{(TextView)view.findViewById(R.id.time4), (TextView)view.findViewById(R.id.summary4), (TextView)view.findViewById(R.id.hiLow4), (TextView)view.findViewById(R.id.precipitation4)};
        dailyViews[5] = new TextView[]{(TextView)view.findViewById(R.id.time5), (TextView)view.findViewById(R.id.summary5), (TextView)view.findViewById(R.id.hiLow5), (TextView)view.findViewById(R.id.precipitation5)};
        dailyViews[6] = new TextView[]{(TextView)view.findViewById(R.id.time6), (TextView)view.findViewById(R.id.summary6), (TextView)view.findViewById(R.id.hiLow6), (TextView)view.findViewById(R.id.precipitation6)};

        dailyImages[0] = (ImageView)view.findViewById(R.id.image0);
        dailyImages[1] = (ImageView)view.findViewById(R.id.image1);
        dailyImages[2] = (ImageView)view.findViewById(R.id.image2);
        dailyImages[3] = (ImageView)view.findViewById(R.id.image3);
        dailyImages[4] = (ImageView)view.findViewById(R.id.image4);
        dailyImages[5] = (ImageView)view.findViewById(R.id.image5);
        dailyImages[6] = (ImageView)view.findViewById(R.id.image6);

        dailyIcons[0] = (ImageView)view.findViewById(R.id.icon0);
        dailyIcons[1] = (ImageView)view.findViewById(R.id.icon1);
        dailyIcons[2] = (ImageView)view.findViewById(R.id.icon2);
        dailyIcons[3] = (ImageView)view.findViewById(R.id.icon3);
        dailyIcons[4] = (ImageView)view.findViewById(R.id.icon4);
        dailyIcons[5] = (ImageView)view.findViewById(R.id.icon5);
        dailyIcons[6] = (ImageView)view.findViewById(R.id.icon6);

        //Getting darkSky data
        try {
            weatherData = new WeatherData(sharedPreferences.getString("weatherData", ""));
            updateWeather();
        }catch (JSONException e){
            ((MainActivity)getActivity()).setup();
            e.printStackTrace();
        }

        return view;
    }

    public void updateWeather()throws JSONException{
        // applying weather info
        DecimalFormat df = new DecimalFormat("##");

        //background image
        String icon = weatherData.getCurrently().getIcon();
        icon = icon.replaceAll("-", "_");
        int backgroundID = getResources().getIdentifier("back_" + icon, "drawable", getActivity().getPackageName());
        background.setImageResource(backgroundID);

        // change the color of the text if needed
        if (icon.contains("rain") || icon.contains("night") || icon.equals("cloudy")){
            dailyField.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
        }

        //updating daily weather
        for (int i = 1; i < 8; i ++){

            TextView[] currentRow = dailyViews[i -1]; // get the matching text view
            DailyDataPoint currentData = weatherData.getDaily().getDay(i);

            // set the text values
            String[] date = currentData.getTime().split(" ");  // time formatting
            String day = date[0] + " " + date[1].replace(",", "");

            double precipProbability = currentData.getPrecipProbabilty(); // percip probability
            precipProbability *= 100;
            String hourPrecip =  df.format(precipProbability) + "% ";

            String hourIcon = currentData.getIcon();
            hourIcon = hourIcon.replaceAll("-", "_");
            int hourResID = getResources().getIdentifier(hourIcon , "drawable", getActivity().getPackageName());
            String precipIcon = "ic_" + currentData.getPrecipType();
            int precipResID = getResources().getIdentifier(precipIcon , "drawable", getActivity().getPackageName());
            String highLow = df.format(currentData.getTemperatureMax()) + "\u00b0/" + df.format(currentData.getTemperatureMin()) + "\u00b0";

            // getting the day of the week
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);
            String currentDate = date[2]+ "-" + date[0] + "-" + date[1];
            String currentDay = "";
            try{ // get day of week
                String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                Date date1 = dateFormat.parse(currentDate);
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                int dayNumber = calendar.get(calendar.DAY_OF_WEEK);
                currentDay = days[dayNumber - 1];
            }catch (Exception e){
                System.out.println("DATE FAILED");
                e.printStackTrace();
            }

            // text setting
            currentRow[0].setText(day);
            currentRow[1].setText(currentDay + ":\n" + currentData.getSummary());
            currentRow[2].setText(highLow);
            currentRow[3].setText(hourPrecip);
            dailyImages[i-1].getLayoutParams().height = 125;
            dailyImages[i-1].getLayoutParams().width = 125;
            dailyImages[i-1].setImageResource(hourResID);
            dailyIcons[i-1].setImageResource(precipResID);

            //color changes as if needed
            if (icon.contains("rain") || icon.contains("night") || icon.equals("cloudy")){
                currentRow[0].setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
                currentRow[1].setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
                currentRow[2].setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
                currentRow[3].setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
                dailyIcons[i - 1].setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
            }
        }
    }
}
