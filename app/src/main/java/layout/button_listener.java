package layout;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import net.weatheraf.weatherandroidforecast.MainActivity;
import net.weatheraf.weatherandroidforecast.R;

import org.json.JSONException;

import WeatherAPI.WeatherData;

/**
 * Created by David on 5/1/2017.
 */

public class button_listener extends WakefulBroadcastReceiver {
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
    private SharedPreferences sharedPreferences;
    private static Context thisContext;
    private static int notificationClickId = (int) System.currentTimeMillis();

    @Override
    public void onReceive(Context context, Intent intent){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent thisIntent = weatherService.createIntentStartNotificationService(context);
        context.startService(thisIntent);
        sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

        try {
            weatherData = new WeatherData(sharedPreferences.getString("weatherData", ""));
            String hourIcon = weatherData.getHourly().getHour(0).getIcon();
            hourIcon = hourIcon.replaceAll("-", "_");
            currentID = context.getResources().getIdentifier(hourIcon, "drawable", context.getPackageName());
            Log.d("imageSuff", Integer.toString(currentID));
            String precipIcon = "ic_" + weatherData.getHourly().getHour(0).getPrecipType();
            int precipResID = context.getResources().getIdentifier(precipIcon, "drawable", context.getPackageName());
            hourIcon = weatherData.getHourly().getHour(1).getIcon();
            hourIcon = hourIcon.replaceAll("-", "_");
            hour1ID = context.getResources().getIdentifier(hourIcon, "drawable", context.getPackageName());
            Log.d("imageSuff", Integer.toString(hour1ID));
            precipIcon = "ic_" + weatherData.getHourly().getHour(0).getPrecipType();
            precipResID = context.getResources().getIdentifier(precipIcon, "drawable", context.getPackageName());
            hourIcon = weatherData.getHourly().getHour(0).getIcon();
            hourIcon = hourIcon.replaceAll("-", "_");
            hour2ID = context.getResources().getIdentifier(hourIcon, "drawable", context.getPackageName());
            Log.d("imageSuff", Integer.toString(hour2ID));
            precipIcon = "ic_" + weatherData.getHourly().getHour(0).getPrecipType();
            precipResID = context.getResources().getIdentifier(precipIcon, "drawable", context.getPackageName());
            thisContext = context;
            getWeatherAndDisplay();
            String toastEmote = new String(Character.toChars((0x1F35E)));
            Toast.makeText(thisContext, toastEmote + "Refreshed!", Toast.LENGTH_SHORT).show();
        }
        catch (JSONException e){

        }
    }
    public void getWeatherAndDisplay() {
        try {
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
            Log.d("WeatherNote", "updating");
            remoteView = new RemoteViews(thisContext.getPackageName(), R.layout.notification_view);
            remoteView.setTextViewText(R.id.current, currentTime);
            remoteView.setTextViewText(R.id.hour1, hour1Time);
            remoteView.setTextViewText(R.id.hour2, hour2Time);
            remoteView.setTextViewText(R.id.degCurrent, Double.toString(degCurrentValue) + "\u00b0");
            remoteView.setTextViewText(R.id.degHour1, Double.toString(degHour1Value)+ "\u00b0");
            remoteView.setTextViewText(R.id.degHour2, Double.toString(degHour2Value)+ "\u00b0");
            remoteView.setTextViewText(R.id.precipCurrent, Double.toString(precipCurrentValue) + "%");
            remoteView.setTextViewText(R.id.precipHour1, Double.toString(precipHour1Value) + "%");
            remoteView.setTextViewText(R.id.precipHour2, Double.toString(precipHour2Value)+ "%");
            remoteView.setImageViewResource(R.id.imgCurrent, currentID);
            remoteView.setImageViewResource(R.id.imgHour1, hour1ID);
            remoteView.setImageViewResource(R.id.imgHour2, hour2ID);

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(thisContext);
            builder.setContentTitle("Android Weather Forecast")
                    .setCustomBigContentView(remoteView)
                    .setSmallIcon(R.mipmap.ic_launcher);
            PendingIntent pendingIntent = PendingIntent.getActivity(thisContext,
                    NOTIFICATION_ID_2,
                    new Intent(thisContext, MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(weatherAlarmReceiver.getDeleteIntent(thisContext));
            builder.setOngoing(true);

            notificationClickId = (int) System.currentTimeMillis();
            Intent button_intent = new Intent("refreshed");
            button_intent.putExtra("id", notificationClickId);

            PendingIntent pButton_intent = PendingIntent.getBroadcast(thisContext, 100, button_intent,0);
            remoteView.setOnClickPendingIntent(R.id.refresh, pButton_intent);

            final NotificationManager manager = (NotificationManager) thisContext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID_2, builder.build());

            Log.i(getClass().getSimpleName(), "Success. precipProbability is " + precipProbability);
        } catch (JSONException e) {
            Log.i(getClass().getSimpleName(), "Error getting weather/sharedPrefs");
        }
    }
}
