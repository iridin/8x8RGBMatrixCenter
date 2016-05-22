package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.BT_COMMAND_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.BT_DATA_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.REQUEST_SEND;
import static cz.cuni.mff.a8x8rgbmatrixcenter.ColorSelectionView.COLOR_COUNT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_HEIGHT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_WIDTH;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class MatrixFragment extends Fragment implements View.OnClickListener  {

    private MatrixActivity mActivity;
    private ColorSelectionView paletteView;
    private LEDView[] leds = new LEDView[LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH];
    private int[] initialColors;


    public MatrixFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.matrix_layout, container, false);

        // Initialize Color palette
        paletteView = (ColorSelectionView) rootView.findViewById(R.id.colorSelectionView);
        paletteView.setActivity(getActivity());
        for(int i = 0; i < COLOR_COUNT; i++) {
            View view = inflater.inflate(R.layout.color_layout, paletteView, false);
            ColorView colorView = (ColorView) view.findViewById(R.id.color_view);
            colorView.setParentView(paletteView);
            paletteView.addView(colorView);
        }

        // Initialize LED matrix
        MatrixView matrixView = (MatrixView) rootView.findViewById(R.id.matrixView);
        for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
            View view = inflater.inflate(R.layout.led_layout, matrixView, false);
            LEDView ledView = (LEDView) view.findViewById(R.id.led_view);
            if(initialColors != null && initialColors.length == LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH){
                ledView.setColor(initialColors[i]);
            }

            ledView.setLedMatrix(matrixView);
            matrixView.addView(ledView);
            ledView.setOnClickListener(this);

            leds[i] = ledView;
        }

        // Register buttons
        Button clearButton = (Button) rootView.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(this);

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
        this.initialColors = colors;
    }

    public void setActivity(MatrixActivity activity){
        mActivity = activity;
    }

    @Override // TODO
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.led_view:
                if(paletteView == null){
                    return;
                }

                LEDView ledView = (LEDView) v;
                int color = paletteView.getColor();
                ledView.setColor(color);
                ledView.invalidate();

                sendData();
                break;
            case R.id.clear_button:
                for(LEDView view : leds){
                    view.setColor(Color.BLACK);
                    view.invalidate();
                }

                sendData();
                break;
            default:
                throw new IllegalStateException(String.format(
                        "The %d view (ID) not supported by the MatrixFragment.Button.OnClickListener",
                        v.getId()));
        }
    }

    private void sendData(){
        BluetoothDevice btDevice = mActivity.getConnectedDevice();
        if(btDevice != null) {
            int colors[] = new int[LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH];
            for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++){
                colors[i] = leds[i].getColor();
            }

            Intent intent = new Intent(mActivity, BluetoothService.class);
            intent.putExtra(BT_COMMAND_KEY, REQUEST_SEND);
            intent.putExtra(BT_DATA_KEY, colors);
            mActivity.startService(intent);
        }
    }
}

