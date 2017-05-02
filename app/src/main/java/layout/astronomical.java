package layout;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.weatheraf.weatherandroidforecast.R;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import WeatherAPI.DailyDataPoint;
import WeatherAPI.WeatherData;

import static java.lang.Math.ceil;

/**
 * A simple {@link Fragment} subclass.
 */
public class astronomical extends Fragment{

    private TextView dateView, setTime, riseTime, moonPhase;
    private WeatherData weatherData;
    private ProgressBar pBar;
    private ImageView moonImg, sunImg, AstronomyBackground;

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
        sunImg.getLayoutParams().width = 200;
        sunImg.getLayoutParams().width = 200;
        AstronomyBackground = (ImageView) view.findViewById(R.id.AstronomyBackground);

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

        DailyDataPoint ddp2 = weatherData.getDaily().getDay(1);

        String sunsetTime = ddp.getSunsetTime();

        System.out.println(sunsetTime);

        long unixTime = 0;
        try {
            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a", Locale.US);
            Date date = dateFormat.parse(sunsetTime);
             unixTime = date.getTime();
        }catch (ParseException p){p.printStackTrace();}

        boolean night;
        if (System.currentTimeMillis() < unixTime) { //day
            riseTime.setText(sunRise[3].substring(0, sunRise[3].length() - 3) + " " + sunRise[4]);
            setTime.setText(sunSet[3].substring(0, sunSet[3].length() - 3) + " " + sunSet[4]);
            sunImg.setImageResource(R.drawable.ic_wb_sunny_black_30dp);
            System.out.println("DAY");
            night = false;
        }
        else { //night
            riseTime.setText(sunSet[3].substring(0, sunSet[3].length() - 3) + " " + sunSet[4]); //moon rise is sunset time from day

            String moonSet[] = ddp2.getSunriseTime().split(" ");
            setTime.setText(moonSet[3].substring(0, moonSet[3].length() - 3) + " " + moonSet[4]); //moon set time is sun rise time from next day

            AstronomyBackground.setImageResource(R.drawable.back_clear_night);
            pBar.getProgressDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
            dateView.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            setTime.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            riseTime.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            moonPhase.setTextColor(ContextCompat.getColor(getActivity(), R.color.rain));
            System.out.println("NIGHT");
            night = true;
        }

        Double phase = ddp.getMoonPhase();

        //Modified to accommodate larger sets for given perception of moon
        //on a given night. Went two above and below quarterly values.
        String moon = "Moon Phase: ";
        String moonIcon;
        if(phase <= 0.01){
            moon += "New Moon";
            moonIcon = "moon_new";
        }
        else if(phase <= 0.22){
            moon += "Waxing Crescent";
            moonIcon = "moon_cresent";
        }
        else if(phase <= 0.27){
            moon += "First Quarter";
            moonIcon = "moon_quarter";
        }
        else if(phase <= 0.47){
            moon += "Waxing Gibbous";
            moonIcon = "moon_gibbous";
        }
        else if(phase <= 0.52){
            moon += "Full Moon";
            moonIcon = "moon_full";
        }
        else if(phase <= 0.72){
            moon += "Waning Gibbous";
            moonIcon = "moon_gibbous";
            moonImg.setRotation(180.0f);
            if (night) sunImg.setRotation(180.0f);
        }
        else if(phase <= 0.77){
            moon += "Last Quarter";
            moonIcon = "moon_quarter";
            moonImg.setRotation(180.0f);
            if (night) sunImg.setRotation(180.0f);
        }
        else{
            moon += "Waning Crescent " + phase;
            moonIcon = "moon_cresent";
            moonImg.setRotation(180.0f);
            if (night) moonImg.setRotation(180.0f);
        }

        int moonResID = getResources().getIdentifier(moonIcon , "drawable", getActivity().getPackageName());
        moonImg.setImageResource(moonResID);
        if (night) sunImg.setImageResource(moonResID);
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