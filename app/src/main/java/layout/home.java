package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import net.weatheraf.weatherandroidforecast.R;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import WeatherAPI.DataPoint;
import WeatherAPI.WeatherData;


/**
 * A simple {@link Fragment} subclass.
 */
public class home extends Fragment implements Updatable{

    private TextView currentTemp, currentCondition, precipitation;
    private TextView[][] hourlyViews = new TextView[12][]; // 2d array of text views for the hourly forecast.
    private ImageView[] hourlyIcons = new ImageView[12];
    private ImageView conditionImage;
    private WeatherData weatherData;

    public home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        // setting up fields
        currentTemp= (TextView) view.findViewById(R.id.currentTemp);
        currentCondition = (TextView) view.findViewById(R.id.currentCondition);
        precipitation = (TextView) view.findViewById(R.id.precipitation);
        conditionImage = (ImageView) view.findViewById(R.id.currentImageView);

        // setting up hourly forecast stuff
        hourlyViews[0] = new TextView[]{(TextView)view.findViewById(R.id.time0), (TextView)view.findViewById(R.id.summary0), (TextView)view.findViewById(R.id.precipitation0)};
        hourlyViews[1] = new TextView[]{(TextView)view.findViewById(R.id.time1), (TextView)view.findViewById(R.id.summary1), (TextView)view.findViewById(R.id.precipitation1)};
        hourlyViews[2] = new TextView[]{(TextView)view.findViewById(R.id.time2), (TextView)view.findViewById(R.id.summary2), (TextView)view.findViewById(R.id.precipitation2)};
        hourlyViews[3] = new TextView[]{(TextView)view.findViewById(R.id.time3), (TextView)view.findViewById(R.id.summary3), (TextView)view.findViewById(R.id.precipitation3)};
        hourlyViews[4] = new TextView[]{(TextView)view.findViewById(R.id.time4), (TextView)view.findViewById(R.id.summary4), (TextView)view.findViewById(R.id.precipitation4)};
        hourlyViews[5] = new TextView[]{(TextView)view.findViewById(R.id.time5), (TextView)view.findViewById(R.id.summary5), (TextView)view.findViewById(R.id.precipitation5)};
        hourlyViews[6] = new TextView[]{(TextView)view.findViewById(R.id.time6), (TextView)view.findViewById(R.id.summary6), (TextView)view.findViewById(R.id.precipitation6)};
        hourlyViews[7] = new TextView[]{(TextView)view.findViewById(R.id.time7), (TextView)view.findViewById(R.id.summary7), (TextView)view.findViewById(R.id.precipitation7)};
        hourlyViews[8] = new TextView[]{(TextView)view.findViewById(R.id.time8), (TextView)view.findViewById(R.id.summary8), (TextView)view.findViewById(R.id.precipitation8)};
        hourlyViews[9] = new TextView[]{(TextView)view.findViewById(R.id.time9), (TextView)view.findViewById(R.id.summary9), (TextView)view.findViewById(R.id.precipitation9)};
        hourlyViews[10] = new TextView[]{(TextView)view.findViewById(R.id.time10), (TextView)view.findViewById(R.id.summary10), (TextView)view.findViewById(R.id.precipitation10)};
        hourlyViews[11] = new TextView[]{(TextView)view.findViewById(R.id.time11), (TextView)view.findViewById(R.id.summary11), (TextView)view.findViewById(R.id.precipitation11)};

        hourlyIcons[0] = (ImageView)view.findViewById(R.id.image0);
        hourlyIcons[1] = (ImageView)view.findViewById(R.id.image1);
        hourlyIcons[2] = (ImageView)view.findViewById(R.id.image2);
        hourlyIcons[3] = (ImageView)view.findViewById(R.id.image3);
        hourlyIcons[4] = (ImageView)view.findViewById(R.id.image4);
        hourlyIcons[5] = (ImageView)view.findViewById(R.id.image5);
        hourlyIcons[6] = (ImageView)view.findViewById(R.id.image6);
        hourlyIcons[7] = (ImageView)view.findViewById(R.id.image7);
        hourlyIcons[8] = (ImageView)view.findViewById(R.id.image8);
        hourlyIcons[9] = (ImageView)view.findViewById(R.id.image9);
        hourlyIcons[10] = (ImageView)view.findViewById(R.id.image10);
        hourlyIcons[11] = (ImageView)view.findViewById(R.id.image11);

        //Getting darkSky data
        try {
            weatherData = new WeatherData(sharedPreferences.getString("weatherData", ""));
            updateWeather();
        }catch (JSONException e){

            e.getMessage();
        }

        return view;

    }

    public void updateWeather()throws JSONException{
        // applying weather info
        currentTemp.setText(String.valueOf(weatherData.getCurrently().getTemperature() + "\u00b0"));
        currentCondition.setText(weatherData.getCurrently().getSummary());
        final int size = 250;
        //updating hourly weather
        for (int i = 0; i < 12; i ++){

            TextView[] currentRow = hourlyViews[i]; // get the matching text view
            DataPoint currentData = weatherData.getHourly().getHour(i);
            // set the text values
            String[] date = currentData.getTime().split(" ");
            String[] hour = date[3].split(":");
            String time = hour[0] + ":" + hour[1] + " " + date[4];
            currentRow[0].setWidth(size);
            currentRow[1].setWidth(size);
            currentRow[0].setText(time);
            currentRow[1].setText(currentData.getSummary()); //// TODO: 4/18/17 format this and add additional data
        }


    }

}
