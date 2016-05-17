package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
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
        for(int i = 0; i < LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH; i++) {
            View view = inflater.inflate(R.layout.led_layout, matrixView, false);
            LEDView ledView = (LEDView) view.findViewById(R.id.led_view);

            ledView.setLedMatrix(matrixView);
            matrixView.addView(ledView);
            ledView.setOnClickListener(matrixView);

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

