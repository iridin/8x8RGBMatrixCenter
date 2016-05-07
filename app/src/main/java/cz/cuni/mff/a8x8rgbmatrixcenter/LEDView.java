package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class LEDView extends View {

    private int color;

    private MatrixView ledMatrix;

    public LEDView(Context context, AttributeSet attrs) {
        super(context, attrs);

        color = Color.BLACK;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float ledMargin = ledMatrix.getLedMargin();

        float xMin = ledMargin;
        float xMax = canvas.getWidth() - ledMargin;
        float yMin = ledMargin;
        float yMax = canvas.getHeight() - ledMargin;
        float xCenter = (xMin + xMax) / 2;
        float yCenter = (yMin + yMax) / 2;
        float radius = Math.max(xMax, yMax) / 2 - 2 * ledMargin;

        RadialGradient gradient = new RadialGradient(xCenter, yCenter, radius,
                color, color & 0x00FFFFFF,
                android.graphics.Shader.TileMode.CLAMP);

        Paint ledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ledPaint.setDither(true);
        ledPaint.setShader(gradient);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(ledMatrix.getLedBackground());

        // Draw background
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);

        // Draw LED
        canvas.drawOval(xMin, yMin, xMax, yMax, ledPaint);
    }

    public void setLedMatrix(MatrixView parentMatrix){
        ledMatrix = parentMatrix;
    }

    public void setColor(int color){
        this.color = color;
    }

    public int getColor(){
        return color;
    }

}
