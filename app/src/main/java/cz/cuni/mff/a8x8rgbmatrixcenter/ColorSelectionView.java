package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.PREFS_NAME;

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

    public static final String COLOR_VIEW_INDEX_KEY = "COLOR_VIEW";
    public static final String COLOR_KEY = "color";
    public static final String COLOR_I_KEY = "Color%d";


    List<ColorView> colorViews = new ArrayList<>();
    private int selectedColor;

    private int colorMargin;
    private Orientation orientation;

    private Activity mActivity;


    public ColorSelectionView(Context context) {
        super(context);

        colorMargin = 0;
        orientation = Orientation.horizontal;

        selectedColor = 0;
        mActivity = null;
    }

    public ColorSelectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        processAttributes(context, attrs);
        mActivity = null;
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorSelectionView, 0, 0);

        try {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            colorMargin = Math.round(a.getDimension(R.styleable.ColorSelectionView_colorMargin, 0)
                    * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            orientation = Orientation.parse(a.getInt(R.styleable.ColorSelectionView_orientation, 0));
        } finally {
            a.recycle();
        }

        selectedColor = 0;
    }

    public void colorViewAdded(ColorView colorView){
        int i = colorViews.size();

        int color = DEFAULT_COLORS.length > i
                ? DEFAULT_COLORS[i]
                : DEFAULT_COLOR;
        if(mActivity != null) {
            SharedPreferences settings = mActivity.getSharedPreferences(PREFS_NAME, 0);
            color = settings.getInt(String.format(COLOR_I_KEY, i), color);
        }

        colorView.setColor(color);
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
        if(mActivity == null){
            return;
        }

        Intent intent = new Intent(mActivity, CustomColorActivity.class);
        intent.putExtra(COLOR_KEY, view.getColor());
        intent.putExtra(COLOR_VIEW_INDEX_KEY, colorViews.indexOf(view));
        intent.putExtra(MatrixActivity.INTENT_CALLER_KEY, this.getClass().getName());
        mActivity.startActivityForResult(intent, 0);
    }

    public void onActivityResult(Intent data){
        int index = data.getIntExtra(COLOR_VIEW_INDEX_KEY, -1);
        int color = data.getIntExtra(COLOR_KEY, 0);
        if(index >= 0){
            SharedPreferences settings = mActivity.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(String.format(COLOR_I_KEY, index), color);
            // Commit the edits!
            editor.commit();

            ColorView view = colorViews.get(index);
            view.setColor(color);
            view.invalidate();
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        assert(getChildCount() == COLOR_COUNT);
        if(getChildCount() == 0){
            return;
        }

        switch(orientation) {
            case horizontal: {
                final int colorSize = (getWidth() - (COLOR_COUNT + 1) * colorMargin) / COLOR_COUNT;

                final int top = colorMargin;
                final int bottom = colorMargin + colorSize;
                for (int i = 0; i < COLOR_COUNT; i++) {
                    final int left = (i + 1) * colorMargin + i * colorSize;
                    final int right = (i + 1) * colorMargin + (i + 1) * colorSize;

                    getChildAt(i).layout(left, top, right, bottom);
                }
            }
                break;
            case vertical: {
                final int colorSize = (getHeight() - (COLOR_COUNT + 1) * colorMargin) / COLOR_COUNT;

                final int left = colorMargin;
                final int right = colorMargin + colorSize;
                for (int i = 0; i < COLOR_COUNT; i++) {
                    final int top = (i + 1) * colorMargin + i * colorSize;
                    final int bottom = (i + 1) * colorMargin + (i + 1) * colorSize;

                    getChildAt(i).layout(left, top, right, bottom);
                }
                break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int colorSize = (widthMeasureSpec - (COLOR_COUNT + 1) * colorMargin) / COLOR_COUNT;
        int height = 2*colorMargin + colorSize;
        switch(orientation) {
            case horizontal:
                setMeasuredDimension(widthMeasureSpec, (int) Math.ceil(height));
                break;
            case vertical:
                setMeasuredDimension((int) Math.ceil(height), heightMeasureSpec);
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(orientation == Orientation.vertical){
            canvas.rotate(-90);
            canvas.translate(-getHeight(), 0);
        }
        super.onDraw(canvas);
    }

    public void setActivity(Activity activity){
        mActivity = activity;
    }

    public int getColor(){
        return colorViews.get(selectedColor).getColor();
    }
}
