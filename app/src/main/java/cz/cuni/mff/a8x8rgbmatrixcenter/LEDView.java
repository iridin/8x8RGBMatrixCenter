package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by iridi on 19.04.2016.
 */
public class LEDView extends View {

    public static final int COLOR_MIN = 0;
    public static final int COLOR_MAX = 255;

    private int red;
    private int green;
    private int blue;

    private MatrixView ledMatrix;

    public LEDView(Context context, AttributeSet attrs) {
        super(context, attrs);

        red = COLOR_MAX;
        green = COLOR_MIN;
        blue = COLOR_MIN;
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
                Color.argb(COLOR_MAX, red, green, blue), Color.argb(COLOR_MIN, red, green, blue),
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

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        if(red < COLOR_MIN){
            this.red = COLOR_MIN;
            return;
        }
        if(red > COLOR_MAX){
            this.red = COLOR_MAX;
            return;
        }

        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        if(green < COLOR_MIN){
            this.green = COLOR_MIN;
            return;
        }
        if(green > COLOR_MAX){
            this.green = COLOR_MAX;
            return;
        }

        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        if(blue < COLOR_MIN){
            this.blue = COLOR_MIN;
            return;
        }
        if(blue > COLOR_MAX){
            this.blue = COLOR_MAX;
            return;
        }

        this.blue = blue;
    }

    public void setLedMatrix(MatrixView parentMatrix){
        ledMatrix = parentMatrix;
    }
}
