package layout;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.weatheraf.weatherandroidforecast.MainActivity;
import net.weatheraf.weatherandroidforecast.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class settings extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner unitSpinner;
    private SharedPreferences sharedPreferences;
    private boolean metric;
    private EditText latitude, longitude;
    private Button submitButton;

    public settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        latitude = (EditText) view.findViewById(R.id.latitude);
        longitude = (EditText) view.findViewById(R.id.longitude);

        latitude.setText(String.valueOf(sharedPreferences.getFloat("latitude", 0)));
        longitude.setText(String.valueOf(sharedPreferences.getFloat("longitude", 0)));


        submitButton = (Button) view.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(this);

        unitSpinner = (Spinner) view.findViewById(R.id.unitSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        metric = sharedPreferences.getBoolean("metric", false);
        if (metric) unitSpinner.setSelection(1);
        else unitSpinner.setSelection(0);

        unitSpinner.setOnItemSelectedListener(this);
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
        Log.d("METRIC SELECTION", String.valueOf(metric));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        metric = sharedPreferences.getBoolean("metric", false);
        if (metric) unitSpinner.setSelection(1);
        else unitSpinner.setSelection(0);

    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Float latitudeF = Float.valueOf(latitude.getText().toString());
        Float longitudeF = Float.valueOf(longitude.getText().toString());
        if (latitudeF == 0 || longitudeF == 0){
            //use default for huntington
            latitudeF = 38.4192f;
            longitudeF = -82.4452f;
            Toast.makeText(getActivity().getApplicationContext(), "Default Location Used", Toast.LENGTH_SHORT).show();

        }
        prefsEditor.putFloat("latitude", latitudeF);
        prefsEditor.putFloat("longitude", longitudeF);
        prefsEditor.apply();

        //close keyboard
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        ((MainActivity)getActivity()).getWeatherData(new home());
    }
}
