package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_WIDTH;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_HEIGHT;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class MatrixFragment extends Fragment {

    public MatrixFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.matrix_layout, container, false);

        // Initialize LED matrix
        MatrixView matrixView = (MatrixView) rootView.findViewById(R.id.matrixView);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
            View view = layoutInflater.inflate(R.layout.led_layout, matrixView, false);
            LEDView ledView = (LEDView) view.findViewById(R.id.led_view);
            ledView.setLedMatrix(matrixView);
            matrixView.addView(ledView);
        }
        return rootView;
    }
}

