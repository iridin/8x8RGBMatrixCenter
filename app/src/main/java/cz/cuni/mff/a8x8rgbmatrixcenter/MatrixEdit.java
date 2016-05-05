package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_HEIGHT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_WIDTH;

public class MatrixEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix_edit);

        MatrixView matrixView = (MatrixView) findViewById(R.id.matrixView);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view;

        for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
            view = layoutInflater.inflate(R.layout.led_layout, matrixView, false);
            LEDView ledView = (LEDView) view.findViewById(R.id.led_view);
            ledView.setLedMatrix(matrixView);
            matrixView.addView(ledView);
        }
    }
}
