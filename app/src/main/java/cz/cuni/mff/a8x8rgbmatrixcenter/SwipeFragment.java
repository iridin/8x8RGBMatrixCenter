package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.BT_COMMAND_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.BT_DATA_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.REQUEST_SEND;
import static cz.cuni.mff.a8x8rgbmatrixcenter.ColorSelectionView.COLOR_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.ColorSelectionView.COLOR_VIEW_INDEX_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.REQUEST_NEW_COLOR;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_HEIGHT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_WIDTH;
import static cz.cuni.mff.a8x8rgbmatrixcenter.TimedColorService.COLOR_CHAIN_KEY;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class SwipeFragment extends Fragment implements Button.OnClickListener {

    private MatrixActivity mActivity;
    private ColorChainAdapter mColorChainAdapter;
    private Orientation orientation;
    private LEDView[] leds = new LEDView[LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH];
    private Boolean[] fingerIndicator = new Boolean[LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH];


    private class LedOnTouchListener implements View.OnTouchListener {

        private SwipeFragment mParent;

        public LedOnTouchListener(SwipeFragment parent){
            mParent = parent;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP){
                for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
                    if(fingerIndicator[i]) {
                        fingerIndicator[i] = false;
                        callTimedColorService(i);
                    }
                }
                return true;
            }

            for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
                LEDView view = mParent.leds[i];
                Rect outRect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                if(outRect.contains((int)event.getX(), (int)event.getY())) {
                    if(!fingerIndicator[i]) {
                        fingerIndicator[i] = true;
                        //callBluetoothService(i, Color.RED);
                        callBluetoothService();
                        view.setColor(Color.RED);
                        view.invalidate();
                    }
                } else if (fingerIndicator[i]){
                    fingerIndicator[i] = false;
                    callTimedColorService(i);
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

        // Remember orientation
        orientation =
                getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
            ? Orientation.horizontal
            : Orientation.vertical;

        // Initialize list of colors
        RecyclerView colorChain = (RecyclerView) rootView.findViewById(R.id.colors_chain);
        colorChain.setHasFixedSize(true);
        //orientation = colorChain.getLayoutParams().
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        if(orientation == Orientation.horizontal) {
            layoutManager.setOrientation(LinearLayout.VERTICAL);
        } else {
            layoutManager.setOrientation(LinearLayout.HORIZONTAL);
        }
        colorChain.setLayoutManager(layoutManager);
        mColorChainAdapter = new ColorChainAdapter(savedInstanceState);
        mColorChainAdapter.setActivity(mActivity);
        colorChain.setAdapter(mColorChainAdapter);
        // Assign ItemTouchHelper
        int remSwipeDirections = orientation == Orientation.horizontal
                ? ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                : ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, remSwipeDirections) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mColorChainAdapter.remove(viewHolder);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(colorChain);

        // Initialize Add button
        FloatingActionButton addButton = (FloatingActionButton) rootView.findViewById(R.id.colors_chain_add);
        addButton.setOnClickListener(this);

        // Initialize LED matrix
        MatrixView matrixView = (MatrixView) rootView.findViewById(R.id.matrixView);
        matrixView.setOnTouchListener(new LedOnTouchListener(this));
        for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
            View view = inflater.inflate(R.layout.led_layout, matrixView, false);
            LEDView ledView = (LEDView) view.findViewById(R.id.led_view);

            ledView.setLedMatrix(matrixView);
            matrixView.addView(ledView);

            leds[i] = ledView;
        }

        // Initialize finger indicators
        for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
            fingerIndicator[i] = false;
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save current state
        mColorChainAdapter.saveInstanceState(savedInstanceState);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(mActivity == null){
            return;
        }

        Intent intent = new Intent(mActivity, CustomColorActivity.class);
        intent.putExtra(COLOR_KEY, Color.BLACK);
        intent.putExtra(COLOR_VIEW_INDEX_KEY, 0);
        mActivity.startActivityForResult(intent, REQUEST_NEW_COLOR);
    }

    public void setActivity(MatrixActivity activity){
        mActivity = activity;
    }

    public void addColor(int color){
        mColorChainAdapter.add(color);
    }

    public void setLedColor(int ledIndex, int color){
        leds[ledIndex].setColor(color);
        leds[ledIndex].invalidate();

        callBluetoothService();
    }

    private void callTimedColorService(int ledIndex){
        Intent intent = new Intent(mActivity, TimedColorService.class);
        intent.putExtra(COLOR_CHAIN_KEY, mColorChainAdapter.getColorChain(ledIndex));
        mActivity.startService(intent);
    }

    private void callBluetoothService(){ // TODO: send only one LED value
        if(mActivity != null && mActivity.getConnectedDevice() != null) {
            int colors[] = new int[LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH];
            for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++){
                colors[i] = leds[i].getColor();
            }

            Intent intent = new Intent(mActivity, BluetoothService.class);
            intent.putExtra(BT_COMMAND_KEY, REQUEST_SEND);
            intent.putExtra(BT_DATA_KEY, colors);
            mActivity.startService(intent);
        }
    }


}

