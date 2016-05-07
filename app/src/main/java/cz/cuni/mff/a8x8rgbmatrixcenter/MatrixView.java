package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class MatrixView extends ViewGroup  implements View.OnClickListener {

    public static final int LED_ARRAY_WIDTH = 8;
    public static final int LED_ARRAY_HEIGHT = 8;

    private float ledMargin = 0;
    private int ledBackground = Color.WHITE;

    private ColorSelectionView colorSelection = null;

    public MatrixView(Context context) {
        super(context);
    }

    public MatrixView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatrixView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        processAttributes(context, attrs);
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.MatrixView, 0, 0);

        try {
            ledMargin = a.getDimension(R.styleable.MatrixView_ledMargin, 0);
            ledBackground = a.getColor(R.styleable.MatrixView_ledBackground, Color.WHITE);

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int min_dimension = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(min_dimension, min_dimension);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        assert(getChildCount() == LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH);

        int width = getWidth() / LED_ARRAY_WIDTH;
        int height = getHeight() / LED_ARRAY_HEIGHT;

        int i = 0;
        for(int row = 0; row < LED_ARRAY_WIDTH; row++){
            for(int column = 0; column < LED_ARRAY_HEIGHT; column++){
                int led_l = row * width;
                int led_r = led_l + width;
                int led_t = column * height;
                int led_b = led_t + height;

                getChildAt(i).layout(led_l, led_t, led_r, led_b);
                i++;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public float getLedMargin(){
        return ledMargin;
    }

    public int getLedBackground(){
        return ledBackground;
    }

    public void setColorSelection(ColorSelectionView view){
        colorSelection = view;
    }

    @Override
    public void onClick(View v) {
        if(colorSelection == null){
            return;
        }

        LEDView ledView = (LEDView) v;
        int color = colorSelection.getColor();
        ledView.setColor(color);
        ledView.invalidate();

    }
}
