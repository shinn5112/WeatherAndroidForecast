package layout;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.util.Arrays;
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
    private ImageView moonImg, sunImg;

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
        sunImg = (ImageView) view.findViewById(R.id.sunImg);

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

        //TODO moon rise and set
        String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
        String[] hour = sunSet[3].split(":");
        int hr = Integer.valueOf(hour[0]) + 12;
        String newHour = hr + ":" + hour[1];
        System.out.println(currentTime.compareTo(newHour));

        boolean night;
        if (currentTime.compareTo(newHour) < 0) { //day
            riseTime.setText(sunRise[3].substring(0, sunRise[3].length() - 3) + " " + sunRise[4]);
            setTime.setText(sunSet[3].substring(0, sunSet[3].length() - 3) + " " + sunSet[4]);

            sunImg.setImageResource(R.drawable.ic_wb_sunny_black_30dp);

            night = false;
        }
        else { //night
            riseTime.setText(sunSet[3].substring(0, sunSet[3].length() - 3) + " " + sunSet[4]); //sunset time from day

            DailyDataPoint ddp2 = weatherData.getDaily().getDay(1);
            String moonSet[] = ddp2.getSunriseTime().split(" ");
            setTime.setText(moonSet[3].substring(0, moonSet[3].length() - 3) + " " + moonSet[4]); //sun rise time from next day

            night = true;
        }

        Double phase = ddp.getMoonPhase();

        //Modified to accommodate larger sets for given perception of moon
        //on a given night. Went two above and below quarterly values.
        String moon = "Moon Phase: ";
        moonImg.setColorFilter(Color.argb(255, 255, 255, 255)); // Set color of moon images, white
        if (night) sunImg.setColorFilter(Color.argb(255, 255, 255, 255)); //TODO PROPER MOON COLOR
        //else sunImg.setColorFilter(); //TODO set back to yellow
        if(phase <= 0.01){
            moon += "New Moon";
            moonImg.setImageResource(R.drawable.moon_new);
            if (night) sunImg.setImageResource(R.drawable.moon_new);
        }
        else if(phase <= 0.22){
            moon += "Waxing Crescent";
            moonImg.setImageResource(R.drawable.moon_cresent);
            if (night) sunImg.setImageResource(R.drawable.moon_cresent);
        }
        else if(phase <= 0.27){
            moon += "First Quarter";
            moonImg.setImageResource(R.drawable.moon_quarter);
            if (night) sunImg.setImageResource(R.drawable.moon_quarter);
        }
        else if(phase <= 0.47){
            moon += "Waxing Gibbous";
            moonImg.setImageResource(R.drawable.moon_gibbous);
            if (night) sunImg.setImageResource(R.drawable.moon_gibbous);
        }
        else if(phase <= 0.52){
            moon += "Full Moon";
            moonImg.setImageResource(R.drawable.moon_full);
            if (night) sunImg.setImageResource(R.drawable.moon_full);
        }
        else if(phase <= 0.72){
            moon += "Waning Gibbous";
            moonImg.setImageResource(R.drawable.moon_gibbous);
            moonImg.setRotation(180.0f);
            if (night) {
                sunImg.setImageResource(R.drawable.moon_gibbous);
                sunImg.setRotation(180.0f);
            }
        }
        else if(phase <= 0.77){
            moon += "Last Quarter";
            moonImg.setImageResource(R.drawable.moon_quarter);
            moonImg.setRotation(180.0f);
            if (night) {
                sunImg.setImageResource(R.drawable.moon_quarter);
                sunImg.setRotation(180.0f);
            }
        }
        else{
            moon += "Waning Crescent " + phase;
            moonImg.setImageResource(R.drawable.moon_cresent);
            moonImg.setRotation(180.0f);
            if (night) {
                sunImg.setImageResource(R.drawable.moon_cresent);
                moonImg.setRotation(180.0f);
            }
        }

        moonPhase.setText(moon);

        int sunPosition = sunPosition();

        System.out.println("THE SUN POS " + sunPosition);

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
        System.out.println("THE HR: "+ hours);

        int hr = Integer.parseInt(hours);
        int possibleRound = Integer.parseInt(time.substring(15,16));

        String rise[];
        String set[];

        rise = riseTime.getText().toString().split(" ");
        set = setTime.getText().toString().split(" ");



        String riseTimeHR = rise[0].substring(0, 1);
        int riseHR;
        if(rise[1].charAt(0) == 'P'){
            riseHR = Integer.parseInt(riseTimeHR);
            riseHR += 12;
        }
        else
            riseHR = Integer.parseInt(riseTimeHR);

        System.out.println("THE RISEE HR: "+ riseHR);


        int setHR = 0;
        String setTimeHR = set[0].substring(0, 1);

        if(set[1].charAt(0) == 'P'){
            setHR = Integer.parseInt(setTimeHR);
            setHR += 12;
        }
        else
            setHR = Integer.parseInt(setTimeHR);

        System.out.println("SET HR " + setHR);


        if(hr > riseHR && possibleRound >=3)
            hr++;

        if(hr > setHR || hr <= riseHR) { //night moon risen percent
            System.out.println("RUN 224");
            double sunPos = hr - riseHR;
            sunPos *= (24.0/(double)(riseHR - setHR));
            sunPos = ceil(sunPos);
            sunPosition = (int) sunPos;
            return sunPosition;
        }
        if(hr == setHR){
            sunPosition = 24;
            System.out.println("RUN 228");
            return sunPosition;
        }
        else {
            System.out.println("RUN 232");
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