package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_layout, container, false);

        return rootView;
    }
}

