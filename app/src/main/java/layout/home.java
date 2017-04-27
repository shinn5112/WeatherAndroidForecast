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

import java.text.DecimalFormat;

import WeatherAPI.DataPoint;
import WeatherAPI.WeatherData;


/**
 * A simple {@link Fragment} subclass.
 */
public class home extends Fragment{

    private TextView currentTemp, currentCondition, precipitation, highLow, feelsLike, humidity, wind, visibility, cloudCover, hourlyForecast;
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
        feelsLike = (TextView) view.findViewById(R.id.feelsLike);
        humidity = (TextView) view.findViewById(R.id.humidity);
        wind = (TextView) view.findViewById(R.id.wind);
        visibility = (TextView) view.findViewById(R.id.visibility);
        cloudCover = (TextView) view.findViewById(R.id.cloudCover);
        hourlyForecast = (TextView) view.findViewById(R.id.hourlyForecast);

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
            e.printStackTrace();
        }

        return view;

    }

    public void updateWeather()throws JSONException{
        // applying weather info
        DecimalFormat df = new DecimalFormat("##");
        String temp;

        //For current weather

        //Text fields
        currentTemp.setText(String.valueOf(df.format(weatherData.getCurrently().getTemperature()) + "\u00b0"));
        currentCondition.setText(weatherData.getCurrently().getSummary());
        String highLowTemp = df.format(weatherData.getDaily().getDay(0).getTemperatureMax()) + "\u00b0" + "/" + df.format(weatherData.getDaily().getDay(0).getTemperatureMin()) + "\u00b0";
        highLow.setText(highLowTemp);


        temp = df.format(weatherData.getCurrently().getPrecipProbabilty() * 100) + "%";
        precipitation.setText(temp);

        temp = df.format(weatherData.getCurrently().getApparentTemperature()) + "\nFeels Like";
        feelsLike.setText(temp);

        temp = df.format(weatherData.getCurrently().getHumidity() * 100) + "\nHumidity"; //TODO make sure 100% displays correctly
        humidity.setText(temp);

        int windDirection = weatherData.getCurrently().getWindBearing(); //TODO can be null is speed is 0,  so fix in wrapper
        String direction;
        windDirection = 2;
        if (windDirection > 348.75 && windDirection <= 11.25) direction = "N";
        else if (windDirection > 11.25 && windDirection <= 33.75) direction = "NNE";
        else if (windDirection > 33.75 && windDirection <= 56.25) direction = "NE";
        else if (windDirection > 56.25 && windDirection <= 78.75) direction = "ENE";
        else if (windDirection > 78.75 && windDirection <= 101.25) direction = "E";
        else if (windDirection > 101.25 && windDirection <= 123.75) direction = "ESE";
        else if (windDirection > 123.75 && windDirection <= 146.25) direction = "SE";
        else if (windDirection > 146.25 && windDirection <= 168.75) direction = "SSE";
        else if (windDirection > 168.75 && windDirection <= 191.25) direction = "S";
        else if (windDirection > 191.25 && windDirection <= 213.75) direction = "SSW";
        else if (windDirection > 213.75 && windDirection <= 236.25) direction = "SW";
        else if (windDirection > 236.25 && windDirection <= 258.75) direction = "WSW";
        else if (windDirection > 258.75 && windDirection <= 281.25) direction = "W";
        else if (windDirection > 281.25 && windDirection <= 303.75) direction = "WNW";
        else if (windDirection > 303.75 && windDirection <= 326.25) direction = "NW";
        else direction = "NNW"; // windDirection > 326.25 && windDirection <= 348.75


        temp = df.format(weatherData.getCurrently().getWindSpeed()) + "unit" + direction + "\nWind";
        wind.setText(temp);

        //temp = df.format(weatherData.getCurrently().getVisibility()) + "unit \nVisibility"; //TODO a 0 case
        temp = "bad";
        visibility.setText(temp);

        temp = df.format(weatherData.getCurrently().getCloudCover() * 100);
        cloudCover.setText(temp);

        //Images
        temp = weatherData.getCurrently().getIcon();
        temp = temp.replaceAll("-", "_");
        int resID = getResources().getIdentifier(temp , "drawable", getActivity().getPackageName());
        int backgroundID = getResources().getIdentifier("back_" + temp, "drawable", getActivity().getPackageName());
        conditionImage.setImageResource(resID);
        background.setImageResource(backgroundID);
        temp = "ic_" + weatherData.getCurrently().getPrecipType();
        resID = getResources().getIdentifier(temp , "drawable", getActivity().getPackageName());
        precipIcon.setImageResource(resID);


        // changing text color if needed
        temp = weatherData.getCurrently().getIcon();
        if (temp.contains("rain") || temp.contains("night") || temp.equals("cloudy")){
            currentTemp.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            currentCondition.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            precipitation.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            highLow.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            feelsLike.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            humidity.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            wind.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            visibility.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            hourlyForecast.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            precipIcon.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint


        }


        //updating hourly weather
        for (int i = 1; i < 13; i ++){

            TextView[] currentRow = hourlyViews[i - 1]; // get the matching text view
            DataPoint currentData = weatherData.getHourly().getHour(i); // get matching data that is for the next hour on
            // set the text values

            String[] date = currentData.getTime().split(" ");  // time formatting
            String[] hour = date[3].split(":");
            String time = hour[0] + ":" + hour[1] + " " + date[4];

            String hourPrecip =  df.format(currentData.getPrecipProbabilty() * 100) + "% ";

            // setting icons
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

            // text setting and image formatting
            currentRow[0].setText(time);
            currentRow[1].setText(currentData.getSummary() +"\nTemp: " + df.format(currentData.getTemperature()) + "\u00b0");
            currentRow[2].setText(hourPrecip);
            hourlyImages[i - 1].getLayoutParams().height = 125;
            hourlyImages[i - 1].getLayoutParams().width = 125;
            hourlyImages[i - 1].setImageResource(hourResID);
            hourlyIcons[i - 1].getLayoutParams().height = 50;
            hourlyIcons[i - 1].getLayoutParams().width = 50;
            hourlyIcons[i - 1].setImageResource(precipResID);

            //formatting text color if needed, this goes off the current background image
            if (temp.contains("rain") || temp.contains("night") || temp.equals("cloudy")){
                currentRow[0].setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
                currentRow[1].setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
                currentRow[2].setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
                hourlyIcons[i - 1].setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint

            }
        }


    }

}
