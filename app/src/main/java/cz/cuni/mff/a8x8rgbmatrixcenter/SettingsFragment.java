package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_layout, container, false);

        Activity activity = getActivity();

        // Create theme options
        Spinner spinner = (Spinner) rootView.findViewById(R.id.theme_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.theme_items, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        ThemeSpinnerListener themeSpinnerListener = new ThemeSpinnerListener(activity);
        spinner.setOnItemSelectedListener(themeSpinnerListener);

        // Set selected value
        String themeName = themeSpinnerListener.themeToString(MatrixActivity.currentTheme);
        int spinnerPosition = adapter.getPosition(themeName);
        spinner.setSelection(spinnerPosition);

        return rootView;
    }
}

