package layout;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import net.weatheraf.weatherandroidforecast.MainActivity;
import net.weatheraf.weatherandroidforecast.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class settings extends Fragment implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, TextWatcher{

    private Spinner unitSpinner;
    private SharedPreferences sharedPreferences;
    private boolean metric;
    private EditText zipCode;
    private Switch gpsSwitch;
    private View view;

    public settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        zipCode = (EditText) view.findViewById(R.id.zipCode);

        zipCode.setText(sharedPreferences.getString("zip", " "));

        zipCode.addTextChangedListener(this);

        unitSpinner = (Spinner) view.findViewById(R.id.unitSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        metric = sharedPreferences.getBoolean("metric", false);
        if (metric) unitSpinner.setSelection(1);
        else unitSpinner.setSelection(0);

        unitSpinner.setOnItemSelectedListener(this);

        gpsSwitch = (Switch) view.findViewById(R.id.gpsSwitch);
        gpsSwitch.setOnCheckedChangeListener(this);
        gpsSwitch.setChecked(sharedPreferences.getBoolean("gps", false));
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        unitSpinner.setSelection(position);
        if (position == 1) metric = true;
        else metric = false;
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean("metric", metric);
        prefsEditor.apply();

        ((MainActivity) getActivity()).getWeatherData(null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        metric = sharedPreferences.getBoolean("metric", false);
        if (metric) unitSpinner.setSelection(1);
        else unitSpinner.setSelection(0);

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            ((MainActivity) getActivity()).getPermission();
        }
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean("gps", isChecked);
        prefsEditor.apply();
        ((MainActivity) getActivity()).getWeatherData(null);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!sharedPreferences.getBoolean("gps", false) && s.length() == 5) {
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();

            Float latitudeF = sharedPreferences.getFloat("latitude", 0);
            Float longitudeF = sharedPreferences.getFloat("longitude", 0);

            //zip code location
            final Geocoder geocoder = new Geocoder(getActivity());
            final String zip = String.valueOf(zipCode.getText().toString());

            try {
                List<Address> addresses = geocoder.getFromLocationName(zip, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    // Use the address as needed
                    latitudeF = (float) address.getLatitude();
                    longitudeF = (float) address.getLongitude();
                } else {
                    // Display appropriate message when Geocoder services are not available
                    Toast.makeText(getActivity(), "Unable to geocode zip code", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                // handle exception
            }


            if (latitudeF == 0 || longitudeF == 0) {
                //use default for huntington
                latitudeF = 38.4192f;
                longitudeF = -82.4452f;
                Toast.makeText(getActivity().getApplicationContext(), "Default Location Used", Toast.LENGTH_SHORT).show();

            }

            prefsEditor.putString("zip", zip);
            prefsEditor.putFloat("latitude", latitudeF);
            prefsEditor.putFloat("longitude", longitudeF);
            prefsEditor.apply();


            ((MainActivity) getActivity()).getWeatherData(null);
        }
    }
}
