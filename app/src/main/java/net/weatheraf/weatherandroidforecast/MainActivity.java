package net.weatheraf.weatherandroidforecast;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;
import android.zetterstrom.com.forecast.models.Forecast;

import com.google.gson.Gson;

import org.json.JSONException;

import WeatherAPI.WeatherData;
import layout.Updatable;
import layout.astronomical;
import layout.forecast;
import layout.home;
import layout.settings;
import layout.setup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String API_KEY = "43b285af3fdc21e9db38a2c02383d78c";
    private WeatherData weatherData;
    private FragmentManager fragmentManager = getFragmentManager();
    private static SharedPreferences sharedPreferences;

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
        getWeatherData();
        getSupportActionBar().setTitle("Huntington, WV");

        // showing home fragment
        fragmentManager.beginTransaction().replace(R.id.main, new home()).commit();
    }

    private void getWeatherData(){
        // setting up the API
        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder(API_KEY)
                        .setCacheDirectory(getCacheDir())
                        .build();
        ForecastClient.create(configuration);

        //getting the forecast
        double latitude = 38.4192;
        double longitude = -82.4452;
        ForecastClient.getInstance()
                .getForecast(latitude, longitude, new Callback<Forecast>() {
                    @Override
                    public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response) {
                        if (response.isSuccessful()) {
                            try {
                                Gson gson = new Gson();
                                weatherData = new WeatherData(gson.toJson(response.body()));
                                insertWeatherData();
                            }catch (Exception e){
                                e.printStackTrace();
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

    @Override
    public void onResume(){
        super.onResume();
        try {
            weatherData = new WeatherData(sharedPreferences.getString("weatherData", ""));
            //System.out.println(weatherData.getCurrently().getSummary());
        }catch (JSONException e){

            e.getMessage();
        }
        //// TODO: 4/20/17 remove debug
    }

    @Override
    public void onPause(){
        super.onPause();
        insertWeatherData();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //refresh
            getWeatherData();
            Fragment fragment = getFragmentManager().findFragmentById(R.id.main);
            if ( fragment instanceof Updatable){
                try {
                    ((Updatable) fragment).updateWeather();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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

    public void setup(){
        fragmentManager.beginTransaction().replace(R.id.main, new setup()).commit();
    }
}
