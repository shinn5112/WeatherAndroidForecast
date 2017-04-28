package layout;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.weatheraf.weatherandroidforecast.R;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

import WeatherAPI.DailyDataPoint;
import WeatherAPI.WeatherData;

import static java.lang.Math.ceil;

/**
 * A simple {@link Fragment} subclass.
 */
public class astronomical extends Fragment{

    private TextView dateView;
    private TextView setTime;
    private TextView riseTime;
    private TextView moonPhase;
    private WeatherData weatherData;
    private ProgressBar pBar;
    private ImageView moonImg;

    public astronomical() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_astronomical, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        dateView = (TextView) view.findViewById(R.id.dateView);
        setTime = (TextView) view.findViewById(R.id.setTime);
        riseTime = (TextView) view.findViewById(R.id.riseTime);
        moonPhase = (TextView) view.findViewById(R.id.moonPhase);
        pBar = (ProgressBar) view.findViewById(R.id.circular_progress_bar);
        moonImg = (ImageView) view.findViewById(R.id.moonImg);

        try {
            weatherData = new WeatherData(sharedPreferences.getString("weatherData", ""));
            setValues();
        }
        catch(JSONException e){

        }
        return view;
    }

    private void setValues () throws JSONException{
        DailyDataPoint ddp = weatherData.getDaily().getDay(0);

        String sunRise[] = ddp.getSunriseTime().split(" ");
        String sunSet[] = ddp.getSunsetTime().split(" ");

        dateView.setText(sunRise[0] + " " + sunRise[1] + " " + sunRise[2]);

        riseTime.setText(sunRise[3] + " " + sunRise[4]);
        setTime.setText(sunSet[3] + " " + sunSet[4]);

        Double phase = ddp.getMoonPhase();

        //Modified to accommodate larger sets for given perception of moon
        //on a given night. Went two above and below quarterly values.

        phase = 0.75;
        if(phase <= 0.01){
            moonPhase.setText("New Moon ");
            moonImg.setImageResource(R.drawable.ic_new_moon);
        }
        else if(phase <= 0.22){
            moonPhase.setText("Waxing Cresent ");
            moonImg.setImageResource(R.drawable.ic_moon_waxing_cresent);
        }
        else if(phase <= 0.27){
            moonPhase.setText("First Quarter ");
            moonImg.setImageResource(R.drawable.ic_moon_quarter);
        }
        else if(phase <= 0.47){
            moonPhase.setText("Waxing Gibbous ");
            moonImg.setImageResource(R.drawable.ic_moon_waxing_gibbous);
        }
        else if(phase <= 0.52){
            moonPhase.setText("Full Moon ");
            moonImg.setImageResource(R.drawable.ic_moon_full);
        }
        else if(phase <= 0.72){
            moonPhase.setText("Wanning Gibbous ");
            moonImg.setImageResource(R.drawable.ic_moon_waxing_gibbous);
            moonImg.setRotation(180.0f);
        }
        else if(phase <= 0.77){
            moonPhase.setText("Last Quarter");
            moonImg.setImageResource(R.drawable.ic_moon_quarter);
            moonImg.setRotation(180.0f);
        }
        else{
            moonPhase.setText("Wanning Cresent " + phase);
            moonImg.setImageResource(R.drawable.ic_moon_waxing_cresent);
            moonImg.setRotation(180.0f);
        }

        int sunPosition = sunPosition();

        ObjectAnimator anim = ObjectAnimator.ofInt(pBar, "progress", 0, sunPosition);
        anim.setDuration(1000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }

    //Calculate position of sun given time and use this for progress bar value.
    public int sunPosition(){
        int sunPosition = 0;

        String time = getDateStringFormatted();
        String hours = time.substring(12,14);

        int hr = Integer.parseInt(hours);
        int possibleRound = Integer.parseInt(time.substring(15,16));

        String rise[] = riseTime.getText().toString().split(" ");
        String set[] = setTime.getText().toString().split(" ");

        String riseTimeHR = rise[0].substring(0, 1);
        int riseHR = Integer.parseInt(riseTimeHR);

        int setHR = 0;
        String setTimeHR = set[0].substring(0, 1);

        if(set[1].charAt(0) == 'P'){
            setHR = Integer.parseInt(setTimeHR);
            setHR += 12;
        }
        else
            setHR = Integer.parseInt(setTimeHR);

        if(hr > riseHR && possibleRound >=3)
            hr++;

        if(hr > setHR || hr <= riseHR) {
            return sunPosition;
        }
        else if(hr == setHR){
            sunPosition = 24;
            return sunPosition;
        }
        else {
            double sunPos = hr - riseHR;
            sunPos *= (24.0/(double)(setHR - riseHR));
            sunPos = ceil(sunPos);
            sunPosition = (int) sunPos;
            return sunPosition;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public String getDateStringFormatted() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        return sdf.format(date);
    }
    
}