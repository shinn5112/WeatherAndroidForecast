package layout;
import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Bundle;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.view.View;

import net.weatheraf.weatherandroidforecast.MainActivity;
import net.weatheraf.weatherandroidforecast.R;

import org.json.JSONException;

import WeatherAPI.WeatherData;


/**
 * Created by David on 4/27/2017.
 */

public class weatherService  extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_ID_2 = 2;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static double precipProbability;
    private WeatherData weatherData;
    private RemoteViews remoteView;

    private static double degCurrentValue;
    private static double degHour1Value;
    private static double degHour2Value;
    private static double precipCurrentValue;
    private static double precipHour1Value;
    private static double precipHour2Value;
    private static String currentTime;
    private static String hour1Time;
    private static String hour2Time;
    private static int currentID;
    private static int hour1ID;
    private static int hour2ID;


    public weatherService() {
        super(weatherService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, weatherService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, weatherService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    private void getWeather(){
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        try{
            weatherData = new WeatherData(sharedPreferences.getString("weatherData", ""));
            precipProbability = weatherData.getHourly().getHour(1).getPrecipProbabilty();
            precipProbability *= 100;
            String[] date = weatherData.getHourly().getHour(0).getTime().split(" ");  // time formatting
            String[] hour = date[3].split(":");
            String time = hour[0] + ":" + hour[1] + " " + date[4];
            currentTime = time;
            date = weatherData.getHourly().getHour(1).getTime().split(" ");  // time formatting
            hour = date[3].split(":");
            time = hour[0] + ":" + hour[1] + " " + date[4];
            hour1Time = time;
            date = weatherData.getHourly().getHour(2).getTime().split(" ");  // time formatting
            hour = date[3].split(":");
            time = hour[0] + ":" + hour[1] + " " + date[4];
            hour2Time = time;
            degCurrentValue = weatherData.getHourly().getHour(0).getTemperature();
            degHour1Value = weatherData.getHourly().getHour(1).getTemperature();
            degHour2Value = weatherData.getHourly().getHour(2).getTemperature();
            precipCurrentValue = weatherData.getHourly().getHour(0).getPrecipProbabilty() * 100;
            precipHour1Value = weatherData.getHourly().getHour(1).getPrecipProbabilty() * 100;
            precipHour2Value = weatherData.getHourly().getHour(2).getPrecipProbabilty() * 100;
            String hourIcon = weatherData.getHourly().getHour(0).getIcon();
            hourIcon = hourIcon.replaceAll("-", "_");
            currentID = getResources().getIdentifier(hourIcon , "drawable", getPackageName());
            Log.d("imageSuff", Integer.toString(currentID));
            String precipIcon = "ic_" + weatherData.getHourly().getHour(0).getPrecipType();
            int precipResID = getResources().getIdentifier(precipIcon , "drawable", getPackageName());
            hourIcon = weatherData.getHourly().getHour(1).getIcon();
            hourIcon = hourIcon.replaceAll("-", "_");
            hour1ID = getResources().getIdentifier(hourIcon , "drawable", getPackageName());
            Log.d("imageSuff", Integer.toString(hour1ID));
            precipIcon = "ic_" + weatherData.getHourly().getHour(0).getPrecipType();
            precipResID = getResources().getIdentifier(precipIcon , "drawable", getPackageName());
            hourIcon = weatherData.getHourly().getHour(0).getIcon();
            hourIcon = hourIcon.replaceAll("-", "_");
            hour2ID = getResources().getIdentifier(hourIcon , "drawable", getPackageName());
            Log.d("imageSuff", Integer.toString(hour2ID));
            precipIcon = "ic_" + weatherData.getHourly().getHour(0).getPrecipType();
            precipResID = getResources().getIdentifier(precipIcon , "drawable", getPackageName());





            Log.i(getClass().getSimpleName(), "Success. precipProbability is " + precipProbability);
        }
        catch (JSONException e){
            Log.i(getClass().getSimpleName(), "Error getting weather/sharedPrefs");
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?
        getWeather();
        showNotificationWeather();
        String message = "";
        if (precipProbability > 90) {
            message = "It's highly likely to rain soon, grab an umbrella!";
        } else if (precipProbability > 50) {
            message = "Grab a jacket just in case! 50/50 chance of rain soon";
        } else if (precipProbability > 10) {
            message = "Slight chance of rain soon, be prepared!";
        }
        if(precipProbability > 10) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Heads up!")
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(new long[]{0, 1000, 1000, 1000, 1000})
                    .setLights(Color.BLUE, 3000, 3000)
                    .setTicker("Weather Update");


            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID,
                    new Intent(this, MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(weatherAlarmReceiver.getDeleteIntent(this));

            final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void showNotificationWeather(){
        remoteView = new RemoteViews(getPackageName(), R.layout.notification_view);
        remoteView.setTextViewText(R.id.current, currentTime);
        remoteView.setTextViewText(R.id.hour1, hour1Time);
        remoteView.setTextViewText(R.id.hour2, hour2Time);
        remoteView.setTextViewText(R.id.degCurrent, Double.toString(degCurrentValue) + "\u00b0");
        remoteView.setTextViewText(R.id.degHour1, Double.toString(degHour1Value)+ "\u00b0");
        remoteView.setTextViewText(R.id.degHour2, Double.toString(degHour1Value)+ "\u00b0");
        remoteView.setTextViewText(R.id.precipCurrent, Double.toString(precipCurrentValue) + "%");
        remoteView.setTextViewText(R.id.precipHour1, Double.toString(precipHour1Value) + "%");
        remoteView.setTextViewText(R.id.precipHour2, Double.toString(precipHour2Value)+ "%");
        remoteView.setImageViewResource(R.id.imgCurrent, currentID);
        remoteView.setImageViewResource(R.id.imgHour1, hour1ID);
        remoteView.setImageViewResource(R.id.imgHour2, hour2ID);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Android Weather Forecast")
                .setCustomBigContentView(remoteView)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID_2,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(weatherAlarmReceiver.getDeleteIntent(this));
        builder.setOngoing(true);

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID_2, builder.build());
    }
}
