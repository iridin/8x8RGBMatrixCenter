package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_HEIGHT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_WIDTH;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class SwipeFragment extends Fragment {

    private MatrixActivity mActivity;
    private ColorChainAdapter mColorChainAdapter;
    private LEDView[] leds = new LEDView[LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH];



    private class LedOnTouchListener implements View.OnTouchListener {

        private SwipeFragment mParent;

        public LedOnTouchListener(SwipeFragment parent){
            mParent = parent;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP){
                for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
                    LEDView view = mParent.leds[i];
                    if (view.getColor() != Color.BLACK){
                        view.setColor(Color.BLACK);
                        view.invalidate();
                    }
                }
                return true;
            }

            for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
                LEDView view = mParent.leds[i];
                Rect outRect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                if(outRect.contains((int)event.getX(), (int)event.getY())) {
                    view.setColor(Color.RED);
                    view.invalidate();
                } else if (view.getColor() != Color.BLACK){
                    view.setColor(Color.BLACK);
                    view.invalidate();
                }
            }
            return true;
        }
    }

    public SwipeFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.swipe_layout, container, false);

        // Initialize list of colors
        RecyclerView colorChain = (RecyclerView) rootView.findViewById(R.id.colors_chain);
        colorChain.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayout.HORIZONTAL);
        colorChain.setLayoutManager(layoutManager);
        mColorChainAdapter = new ColorChainAdapter();
        mColorChainAdapter.setActivity(mActivity);
        colorChain.setAdapter(mColorChainAdapter);
        // Assign ItemTouchHelper
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mColorChainAdapter.remove(viewHolder);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ColorChainAdapter.ImageViewHolder){
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(colorChain);


        // Initialize LED matrix
        MatrixView matrixView = (MatrixView) rootView.findViewById(R.id.matrixView);
        matrixView.setColorSelection(null);
        matrixView.setOnTouchListener(new LedOnTouchListener(this));
        for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
            View view = inflater.inflate(R.layout.led_layout, matrixView, false);
            LEDView ledView = (LEDView) view.findViewById(R.id.led_view);

            ledView.setLedMatrix(matrixView);
            matrixView.addView(ledView);

            leds[i] = ledView;
        }

        return rootView;
    }

    public void setActivity(MatrixActivity activity){
        mActivity = activity;
    }

    public void addColor(int color){
        mColorChainAdapter.add(color);
    }

}

