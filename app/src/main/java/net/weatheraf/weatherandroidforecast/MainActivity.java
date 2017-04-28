package net.weatheraf.weatherandroidforecast;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;
import android.zetterstrom.com.forecast.models.Forecast;
import android.zetterstrom.com.forecast.models.Unit;

import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import WeatherAPI.WeatherData;
import layout.astronomical;
import layout.forecast;
import layout.home;
import layout.settings;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private static final String API_KEY = "43b285af3fdc21e9db38a2c02383d78c";
    private WeatherData weatherData;
    private FragmentManager fragmentManager = getFragmentManager();
    private static SharedPreferences sharedPreferences;
    private boolean metric = false;
    private View header;
    private TextView updateTime;

    LocationManager locationManager;

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
        header = navigationView.getHeaderView(0);
        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        updateTime = (TextView)header.findViewById(R.id.updateTime);

        // setup
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        getLocation();

        if (sharedPreferences.getFloat("latitude", 0) == 0 || sharedPreferences.getFloat("longitude", 0) == 0) getWeatherData(new settings());
        else getWeatherData(new home());

    }

    public void getWeatherData(final Fragment fragment){
        // setting up the API
        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder(API_KEY)
                        .setCacheDirectory(getCacheDir())
                        .build();
        ForecastClient.create(configuration);

        getLocation();

        //getting the forecast
        double latitude = sharedPreferences.getFloat("latitude", 0);
        double longitude = sharedPreferences.getFloat("longitude", 0);


        // getting city name
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                getSupportActionBar().setTitle(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        metric = sharedPreferences.getBoolean("metric", false);
        Unit unit;
        if (metric) unit = Unit.SI;
        else unit = Unit.US;

        //lets save some space by not getting these data points
        List<String> excludeList = new ArrayList<>();
        excludeList.add("minutely");

        ForecastClient.getInstance()
                .getForecast(latitude, longitude, null, null, unit, excludeList, false, new Callback<Forecast>() {
                    @Override
                    public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response) {
                        if (response.isSuccessful()) {
                            try {
                                Gson gson = new Gson();
                                weatherData = new WeatherData(gson.toJson(response.body()));
                                insertWeatherData();

                                // update fields
                                String[] timeSplit = weatherData.getCurrently().getTime().split(" ");
                                String[] clock = timeSplit[3].split(":");
                                String time = "Updated: " + timeSplit[0] + " " + timeSplit[1].replace(",", "") + " " + clock[0] + ":" + clock[1] + " " + timeSplit[4];
                                updateTime.setText(time);



                                // set the fragment
                                if (fragment != null) fragmentManager.beginTransaction().replace(R.id.main, fragment).commit();
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
        getWeatherData(null);
        //// TODO: 4/20/17 remove debug
    }

    @Override
    public void onPause(){
        super.onPause();
        insertWeatherData();
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
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


    //for location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    public void getLocation() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

        if (sharedPreferences.getBoolean("gps", false) && EasyPermissions.hasPermissions(this, perms)) {


            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Location location;
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                else{
                    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                    prefsEditor.putBoolean("gps", false);
                    prefsEditor.apply();
                    return; //so last statements aren't reached
                }

                SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                prefsEditor.putFloat("latitude", (float) location.getLatitude());
                prefsEditor.putFloat("longitude", (float) location.getLongitude());
                prefsEditor.apply();
            }
        }
    }

    //@AfterPermissionGranted(RC_LOCATION)
    public void getPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getLocation();

        } else {
            EasyPermissions.requestPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, 1, perms);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
