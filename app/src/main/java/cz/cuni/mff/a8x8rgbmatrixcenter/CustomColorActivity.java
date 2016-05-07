package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class CustomColorActivity extends AppCompatActivity {

    private int color = 0;
    private int ledRed = 0;
    private int ledGreen = 0;
    private int ledBlue = 0;

    private ColorView colorView;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_color_layout);

        intent = getIntent();
        color = intent.getIntExtra(ColorSelectionView.COLOR_KEY, 0);
        // If the state is restored from saved instance
        if(savedInstanceState != null) {
            color = savedInstanceState.getInt(ColorSelectionView.COLOR_KEY);
        }
        ledRed = ColorHelper.color2LedRed(color);
        ledGreen = ColorHelper.color2LedGreen(color);
        ledBlue = ColorHelper.color2LedBlue(color);

        registerRedSeekbar();
        registerGreenSeekbar();
        registerBlueSeekbar();

        registerCancelButton();
        registerOkButton();

        colorView = (ColorView) findViewById(R.id.colorPreview);
        colorView.setColor(color);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save current state
        savedInstanceState.putInt(ColorSelectionView.COLOR_KEY, color);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void registerOkButton(){
        Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra(ColorSelectionView.COLOR_KEY, color);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void registerCancelButton(){
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void registerRedSeekbar(){
        SeekBar redSeekbar = (SeekBar) findViewById(R.id.red_seekbar);
        redSeekbar.setProgress(ledRed);
        redSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ledRed = progress;
                color = ColorHelper.led2Color(ledRed, ledGreen, ledBlue);

                colorView.setColor(color);
                colorView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    private void registerGreenSeekbar(){
        SeekBar greenSeekbar = (SeekBar) findViewById(R.id.green_seekbar);
        greenSeekbar.setProgress(ledGreen);
        greenSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ledGreen = progress;
                color = ColorHelper.led2Color(ledRed, ledGreen, ledBlue);

                colorView.setColor(color);
                colorView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    private void registerBlueSeekbar(){
        SeekBar blueSeekbar = (SeekBar) findViewById(R.id.blue_seekbar);
        blueSeekbar.setProgress(ledBlue);
        blueSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ledBlue = progress;
                color = ColorHelper.led2Color(ledRed, ledGreen, ledBlue);

                colorView.setColor(color);
                colorView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }
}
