package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class ColorSelectionView extends ViewGroup {

    public static final int COLOR_COUNT = 8;

    private static final int DEFAULT_COLOR = 0xFF000000;
    private static final int[] DEFAULT_COLORS = new int[] {
            0xFF000000,
            0xFFFFFFFF,
            0xFFFF0000,
            0xFF00FF00,
            0xFF0000FF,
            0xFFFFFF00,
            0xFFFF00FF,
            0xFF00FFFF };

    List<ColorView> colorViews = new ArrayList<>();
    private int selectedColor;

    private int colorMargin;

    public ColorSelectionView(Context context) {
        super(context);

        colorMargin = 0;
        selectedColor = 0;
    }

    public ColorSelectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        processAttributes(context, attrs);
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorSelectionView, 0, 0);

        try {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            colorMargin = Math.round(a.getDimension(R.styleable.ColorSelectionView_colorMargin, 0)
                    * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        } finally {
            a.recycle();
        }

        selectedColor = 0;
    }

    public void colorViewAdded(ColorView colorView){
        int i = colorViews.size();

        // Set color
        // TODO: load saved palette
        if(DEFAULT_COLORS.length > i){
            colorView.setColor(DEFAULT_COLORS[i]);
        } else {
            colorView.setColor(DEFAULT_COLOR);
        }

        colorView.setSelected(i == 0);
        colorViews.add(colorView);
    }

    public void colorViewClicked(ColorView view){
        int i = colorViews.indexOf(view);

        if(i >= 0 && i != selectedColor){
            ColorView previousView = colorViews.get(selectedColor);
            previousView.setSelected(false);
            previousView.invalidate();

            selectedColor = i;
            view.setSelected(true);
            view.invalidate();
        }
    }

    public void colorViewLongClicked(ColorView view){

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        assert(getChildCount() == COLOR_COUNT);

        final int colorSize = (getWidth() - (COLOR_COUNT + 1) * colorMargin) / COLOR_COUNT;

        final int top = colorMargin;
        final int bottom = colorMargin + colorSize;
        for(int i = 0; i < COLOR_COUNT; i++) {
            final int left = (i + 1) * colorMargin + i * colorSize;
            final int right = (i + 1) * colorMargin + (i + 1) * colorSize;

            getChildAt(i).layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int colorSize = (widthMeasureSpec - (COLOR_COUNT + 1) * colorMargin) / COLOR_COUNT;
        int height = 2*colorMargin + colorSize;
        setMeasuredDimension(widthMeasureSpec, (int) Math.ceil(height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
