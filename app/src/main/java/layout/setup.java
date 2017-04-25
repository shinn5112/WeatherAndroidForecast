package layout;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.weatheraf.weatherandroidforecast.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class setup extends Fragment implements View.OnClickListener{


    public setup() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        Button restart = (Button) view.findViewById(R.id.restartButton);
        restart.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;

    }


    @Override
    public void onClick(View v) {
        System.exit(0);
    }

}
