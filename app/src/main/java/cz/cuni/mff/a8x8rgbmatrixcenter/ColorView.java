package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class ColorView extends View implements View.OnClickListener, View.OnLongClickListener {

    private final static int BORDER_WIDTH = 5;
    private final static int SELECTED_BORDER_WIDTH = 10;

    private int color;
    private ColorSelectionView parentView;
    private Paint borderPaint;

    public ColorView(Context context) {
        super(context);

        init();
    }

    public ColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        color = 0xFF000000;

        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_WIDTH);

        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int left = 0;
        final int top = 0;
        final int right = canvas.getWidth();
        final int bottom = canvas.getHeight();

        RadialGradient gradient = new RadialGradient(
                canvas.getWidth() / 2,
                canvas.getHeight() / 2,
                canvas.getWidth() / 2,
                color,
                color & 0x00FFFFFF,
                android.graphics.Shader.TileMode.CLAMP);

        Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorPaint.setDither(true);
        colorPaint.setShader(gradient);

        canvas.drawRect(left, top, right, bottom, colorPaint);
        canvas.drawRect(left, top, right, bottom, borderPaint);

    }

    @Override
    public void onClick(View v) {
        parentView.colorViewClicked(this);
    }

    @Override
    public boolean onLongClick(View v) {
        parentView.colorViewLongClicked(this);
        return true;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            borderPaint.setColor(Color.RED);
            borderPaint.setStrokeWidth(SELECTED_BORDER_WIDTH);
        } else {

            borderPaint.setColor(Color.BLACK);
            borderPaint.setStrokeWidth(BORDER_WIDTH);
        }
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setParentView(ColorSelectionView parentView) {
        this.parentView = parentView;
        parentView.colorViewAdded(this);
    }
}
