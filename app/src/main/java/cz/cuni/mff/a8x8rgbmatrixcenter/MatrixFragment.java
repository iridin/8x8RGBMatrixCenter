package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_HEIGHT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_WIDTH;
import static cz.cuni.mff.a8x8rgbmatrixcenter.ColorSelectionView.COLOR_COUNT;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class MatrixFragment extends Fragment {

    private LEDView[] leds = new LEDView[LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH];
    private int[] colors;

    public MatrixFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.matrix_layout, container, false);

        // Initialize Color palette
        ColorSelectionView paletteView = (ColorSelectionView) rootView.findViewById(R.id.colorSelectionView);
        paletteView.setActivity(getActivity());
        for(int i = 0; i < COLOR_COUNT; i++) {
            View view = inflater.inflate(R.layout.color_layout, paletteView, false);
            ColorView colorView = (ColorView) view.findViewById(R.id.color_view);
            colorView.setParentView(paletteView);
            paletteView.addView(colorView);
        }

        // Initialize LED matrix
        MatrixView matrixView = (MatrixView) rootView.findViewById(R.id.matrixView);
        matrixView.setColorSelection(paletteView);
        for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
            View view = inflater.inflate(R.layout.led_layout, matrixView, false);
            LEDView ledView = (LEDView) view.findViewById(R.id.led_view);
            if(colors != null && colors.length == LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH){
                ledView.setColor(colors[i]);
            }

            ledView.setLedMatrix(matrixView);
            matrixView.addView(ledView);
            ledView.setOnClickListener(matrixView);

            leds[i] = ledView;
        }


        return rootView;
    }

    public void saveMatrixState(Bundle savedInstanceState){
        int colors[] = new int[LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH];
        for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
            colors[i] = leds[i].getColor();
        }
        savedInstanceState.putIntArray(MatrixActivity.LED_COLOR_KEY, colors);
    }

    public void setLedColors(int[] colors){
        this.colors = colors;
    }
}

