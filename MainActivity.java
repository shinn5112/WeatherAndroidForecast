package net.weatheraf.weatherandroidforecast;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;
import android.zetterstrom.com.forecast.models.Forecast;

import com.google.gson.Gson;

import org.json.JSONException;

import java.util.List;
import java.util.Locale;

import WeatherAPI.WeatherData;
import layout.astronomical;
import layout.forecast;
import layout.home;
import layout.settings;
import layout.weatherService;
import layout.weatherAlarmReceiver;
import layout.weatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String API_KEY = "43b285af3fdc21e9db38a2c02383d78c";
    private WeatherData weatherData;
    private FragmentManager fragmentManager = getFragmentManager();
    private static SharedPreferences sharedPreferences;
    private boolean metric = false;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // nav drawer stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // setup
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);


        if (sharedPreferences.getFloat("latitude", 0) == 0 || sharedPreferences.getFloat("longitude", 0) == 0) getWeatherData(new settings());
        else getWeatherData(new home());
        //getSupportActionBar().setTitle("Huntington, WV");

        boolean alarmUp = (PendingIntent.getBroadcast(this, 1,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Log.d("myTag", "Alarm is already active");
        }
        else{
            Log.d("myTag", "Alarm is not active");
        }
        if(!isMyServiceRunning(weatherService.class)) {
            weatherAlarmReceiver.setupAlarm(getApplication());
            Log.i(getClass().getSimpleName(), "Service not already running. starting now");
        }
        else{
            Log.i(getClass().getSimpleName(), "Service already running");
        }



    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void getWeatherData(final Fragment fragment){
        // setting up the API
        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder(API_KEY)
                        .setCacheDirectory(getCacheDir())
                        .build();
        ForecastClient.create(configuration);

        //getting the forecast
        double latitude = sharedPreferences.getFloat("latitude", 0);
        double longitude = sharedPreferences.getFloat("longitude", 0);


        // getting city name
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                getSupportActionBar().setTitle(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
            } else {
                // do your staff
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ForecastClient.getInstance()
                .getForecast(latitude, longitude, new Callback<Forecast>() {
                    @Override
                    public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response) {
                        if (response.isSuccessful()) {
                            try {
                                Gson gson = new Gson();
                                weatherData = new WeatherData(gson.toJson(response.body()));
                                insertWeatherData();
                                fragmentManager.beginTransaction().replace(R.id.main, fragment).commit();
                            }catch (Exception e){
                                e.printStackTrace();
                                setup();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Forecast> forecastCall, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();

                    }
                });

    }

    private void insertWeatherData(){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
       
        // // TODO: 4/19/17 Remove debug
        System.out.println("On insert\n"+weatherData.getJson());
        prefsEditor.putString("weatherData", weatherData.getJson());
        prefsEditor.apply();
    }

    public void setup(){
        fragmentManager.beginTransaction().replace(R.id.main, new settings()).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            weatherData = new WeatherData(sharedPreferences.getString("weatherData", ""));
        } catch (JSONException e) {

            e.getMessage();
        }

        metric = sharedPreferences.getBoolean("metric", false);
        //// TODO: 4/20/17 remove debug
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        // handleIntent();
    }

    @Override
    public void onPause(){
        super.onPause();
        insertWeatherData();
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean("metric", metric);
        prefsEditor.apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            //refresh
            Fragment fragment = getFragmentManager().findFragmentById(R.id.main);
            if ( fragment instanceof home){
                getWeatherData(new home());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected( @NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()){
            case R.id.nav_home :
                fragmentManager.beginTransaction().replace(R.id.main, new home()).commit();
                break;
            case R.id.nav_forecast :
                fragmentManager.beginTransaction().replace(R.id.main, new forecast()).commit();
                break;
            case R.id.nav_astronomical :
                fragmentManager.beginTransaction().replace(R.id.main, new astronomical()).commit();
                break;
            case R.id.nav_settings :
                fragmentManager.beginTransaction().replace(R.id.main, new settings()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
