package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import net.weatheraf.weatherandroidforecast.MainActivity;
import net.weatheraf.weatherandroidforecast.R;

import org.json.JSONException;

import java.text.DecimalFormat;

import WeatherAPI.DataPoint;
import WeatherAPI.WeatherData;


/**
 * A simple {@link Fragment} subclass.
 */
public class home extends Fragment{

    private TextView currentTemp, currentCondition, precipitation, highLow;
    private TextView[][] hourlyViews = new TextView[12][]; // 2d array of text views for the hourly forecast.
    private ImageView[] hourlyImages = new ImageView[12];
    private ImageView[] hourlyIcons = new ImageView[12];
    private ImageView conditionImage, precipIcon, background;
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
        highLow = (TextView) view.findViewById(R.id.highLow);
        conditionImage = (ImageView) view.findViewById(R.id.currentImageView);
        precipIcon = (ImageView) view.findViewById(R.id.precipIcon);
        background = (ImageView) view.findViewById(R.id.homeBackground);

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

        hourlyImages[0] = (ImageView)view.findViewById(R.id.image0);
        hourlyImages[1] = (ImageView)view.findViewById(R.id.image1);
        hourlyImages[2] = (ImageView)view.findViewById(R.id.image2);
        hourlyImages[3] = (ImageView)view.findViewById(R.id.image3);
        hourlyImages[4] = (ImageView)view.findViewById(R.id.image4);
        hourlyImages[5] = (ImageView)view.findViewById(R.id.image5);
        hourlyImages[6] = (ImageView)view.findViewById(R.id.image6);
        hourlyImages[7] = (ImageView)view.findViewById(R.id.image7);
        hourlyImages[8] = (ImageView)view.findViewById(R.id.image8);
        hourlyImages[9] = (ImageView)view.findViewById(R.id.image9);
        hourlyImages[10] = (ImageView)view.findViewById(R.id.image10);
        hourlyImages[11] = (ImageView)view.findViewById(R.id.image11);

        hourlyIcons[0] = (ImageView)view.findViewById(R.id.icon0);
        hourlyIcons[1] = (ImageView)view.findViewById(R.id.icon1);
        hourlyIcons[2] = (ImageView)view.findViewById(R.id.icon2);
        hourlyIcons[3] = (ImageView)view.findViewById(R.id.icon3);
        hourlyIcons[4] = (ImageView)view.findViewById(R.id.icon4);
        hourlyIcons[5] = (ImageView)view.findViewById(R.id.icon5);
        hourlyIcons[6] = (ImageView)view.findViewById(R.id.icon6);
        hourlyIcons[7] = (ImageView)view.findViewById(R.id.icon7);
        hourlyIcons[8] = (ImageView)view.findViewById(R.id.icon8);
        hourlyIcons[9] = (ImageView)view.findViewById(R.id.icon9);
        hourlyIcons[10] = (ImageView)view.findViewById(R.id.icon10);
        hourlyIcons[11] = (ImageView)view.findViewById(R.id.icon11);

        //Getting darkSky data
        try {
            ((MainActivity) getActivity()).getWeatherData(null);
            weatherData = new WeatherData(sharedPreferences.getString("weatherData", ""));
            updateWeather();
        }catch (JSONException e){
            ((MainActivity)getActivity()).setup();
            e.getMessage();
        }

        return view;

    }

    public void updateWeather()throws JSONException{
        // applying weather info
        DecimalFormat df = new DecimalFormat("##");

        currentTemp.setText(String.valueOf(df.format(weatherData.getCurrently().getTemperature()) + "\u00b0"));
        currentCondition.setText(weatherData.getCurrently().getSummary());
        String highLowTemp = df.format(weatherData.getDaily().getDay(0).getTemperatureMax()) + "\u00b0" + "/" + df.format(weatherData.getDaily().getDay(0).getTemperatureMin()) + "\u00b0";
        highLow.setText(highLowTemp);

        double precipProbability = weatherData.getCurrently().getPrecipProbabilty();
        precipProbability *= 100;
        String currentPrecipProbability = df.format(precipProbability) + "%";
        precipitation.setText(currentPrecipProbability);
        String icon = weatherData.getCurrently().getIcon();
        icon = icon.replaceAll("-", "_");
        int resID = getResources().getIdentifier(icon , "drawable", getActivity().getPackageName());
        int backgroundID = getResources().getIdentifier("back_" + icon, "drawable", getActivity().getPackageName());
        conditionImage.setImageResource(resID);
        background.setImageResource(backgroundID);
        icon = "ic_" + weatherData.getCurrently().getPrecipType();
        resID = getResources().getIdentifier(icon , "drawable", getActivity().getPackageName());
        precipIcon.setImageResource(resID);

        //updating hourly weather
        for (int i = 1; i < 13; i ++){

            TextView[] currentRow = hourlyViews[i]; // get the matching text view
            DataPoint currentData = weatherData.getHourly().getHour(i);
            // set the text values

            String[] date = currentData.getTime().split(" ");  // time formatting
            String[] hour = date[3].split(":");
            String time = hour[0] + ":" + hour[1] + " " + date[4];

            precipProbability = currentData.getPrecipProbabilty(); // percip probability
            precipProbability *= 100;
            String hourPrecip =  df.format(precipProbability) + "% ";

            String hourIcon = currentData.getIcon();
            hourIcon = hourIcon.replaceAll("-", "_");
            int hourResID = getResources().getIdentifier(hourIcon , "drawable", getActivity().getPackageName());
            String precipIcon = "ic_" + currentData.getPrecipType();
            int precipResID = getResources().getIdentifier(precipIcon , "drawable", getActivity().getPackageName());

            // style shit
            final int size = 300;
            currentRow[0].setWidth(size);
            currentRow[0].setGravity(Gravity.CENTER);
            currentRow[1].setWidth(size);
            currentRow[1].setGravity(Gravity.CENTER);
            currentRow[2].setWidth(size/2);
            currentRow[2].setGravity(Gravity.CENTER);

            // text setting
            currentRow[0].setText(time);
            currentRow[1].setText(currentData.getSummary() +"\nTemp: " + df.format(currentData.getTemperature()) + "\u00b0");
            currentRow[2].setText(hourPrecip);
            hourlyImages[i].getLayoutParams().height = 125;
            hourlyImages[i].getLayoutParams().width = 125;
            hourlyImages[i].setImageResource(hourResID);
            hourlyIcons[i].getLayoutParams().height = 50;
            hourlyIcons[i].getLayoutParams().width = 50;
            hourlyIcons[i].setImageResource(precipResID);
        }


    }

}
