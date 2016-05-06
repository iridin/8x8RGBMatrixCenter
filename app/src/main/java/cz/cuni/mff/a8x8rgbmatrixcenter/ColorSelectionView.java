package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class ColorSelectionView extends View {

    private static final int DEFAULT_COLOR = 0xFF000000;
    private static final int DEFAULT_COLOR_CNT = 8;
    private static final int[] DEFAULT_COLORS = new int[] {
            0xFF000000,
            0xFFFFFFFF,
            0xFFFF0000,
            0xFF00FF00,
            0xFF0000FF,
            0xFFFFFF00,
            0xFFFF00FF,
            0xFF00FFFF };

    private int[] colors;
    private int colorMargin;
    private int defaultColor;
    private int colorCount;

    public ColorSelectionView(Context context) {
        super(context);

        colorMargin = 0;
        defaultColor = 0xFF000000;
        colorCount = DEFAULT_COLOR_CNT;
        colors = new int[colorCount];

        setColors();
    }

    public ColorSelectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        processAttributes(context, attrs);
        setColors();
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorSelectionView, 0, 0);

        try {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            colorMargin = Math.round(a.getDimension(R.styleable.ColorSelectionView_colorMargin, 0)
                    * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            defaultColor = a.getColor(R.styleable.ColorSelectionView_defaultColor, Color.WHITE);
            colorCount = a.getInt(R.styleable.ColorSelectionView_colorCount, DEFAULT_COLOR_CNT);
        } finally {
            a.recycle();
        }

        colors = new int[colorCount];
    }

    private void setColors(){
        for(int i = 0; i < colorCount; i++){
            // TODO: load saved palette
            if(DEFAULT_COLORS.length > i){
                colors[i] = DEFAULT_COLORS[i];
            } else {
                colors[i] = DEFAULT_COLOR;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(colorCount <= 0){
            setMeasuredDimension(0, 0);
            return;
        }

        int colorSize = (widthMeasureSpec - (colorCount + 1) * colorMargin) / colorCount;
        int height = 2*colorMargin + colorSize;
        setMeasuredDimension(widthMeasureSpec, (int) Math.ceil(height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        final int colorSize = (canvas.getWidth() - (colorCount + 1) * colorMargin) / colorCount;

        final int top = colorMargin;
        final int bottom = colorMargin + colorSize;
        for(int i = 0; i < colorCount; i++){
            final int left = (i+1)*colorMargin + i*colorSize;
            final int right = (i+1)*colorMargin + (i+1)*colorSize;

            Paint colorPaint = new Paint();
            colorPaint.setColor(colors[i]);
            colorPaint.setStyle(Paint.Style.FILL);

            canvas.drawRect(left, top, right, bottom, colorPaint);
            canvas.drawRect(left, top, right, bottom, borderPaint);
        }
    }
}
