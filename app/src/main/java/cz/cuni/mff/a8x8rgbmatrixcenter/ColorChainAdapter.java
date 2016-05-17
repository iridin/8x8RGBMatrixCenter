package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static cz.cuni.mff.a8x8rgbmatrixcenter.ColorSelectionView.COLOR_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.ColorSelectionView.COLOR_VIEW_INDEX_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.REQUEST_NEW_COLOR;


/**
 * Created by Dominik Skoda on 11.05.2016.
 */
public class ColorChainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int COLOR_VIEW_TYPE = 0;
    public static final int IMAGE_VIEW_TYPE = 1;

    private List<Integer> colorChain;
    private long defaultDelay = 100;
    private Activity mActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ColorViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

        private ColorChainAdapter parent;

        public ColorView colorView;
        public long delay;

        public ColorViewHolder(View colorView, ColorChainAdapter parent) {
            super(colorView);
            this.colorView = (ColorView) colorView.findViewById(R.id.color_view);
            this.parent = parent;
            delay = parent.defaultDelay;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
            final String selection = (String) adapter.getItemAtPosition(position);
            delay = parent.timeSelectionToMillis(selection);
            Log.i("ColorViewHolder", "Delay: " + delay);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener {

        private ColorChainAdapter parent;

        public ImageViewHolder(View imageView, ColorChainAdapter parent){
            super(imageView);
            imageView.setOnClickListener(this);
            this.parent = parent;
        }

        @Override
        public void onClick(View v) {
            if(parent.mActivity == null){
                return;
            }

            Intent intent = new Intent(parent.mActivity, CustomColorActivity.class);
            intent.putExtra(COLOR_KEY, Color.BLACK);
            intent.putExtra(COLOR_VIEW_INDEX_KEY, 0);
            parent.mActivity.startActivityForResult(intent, REQUEST_NEW_COLOR);
        }

        @Override
        public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
            final String selection = (String) adapter.getItemAtPosition(position);
            parent.defaultDelay = parent.timeSelectionToMillis(selection);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing
        }

    }

    // Provide a suitable constructor
    public ColorChainAdapter() {
        colorChain = new ArrayList<>();
        colorChain.add(Color.RED);
        colorChain.add(Color.GREEN);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;
        switch(viewType) {
            case COLOR_VIEW_TYPE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.color_time_layout, parent, false);
                vh = new ColorViewHolder(v, this);
                break;
            case IMAGE_VIEW_TYPE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.image_time_layout, parent, false);
                vh = new ImageViewHolder(v, this);
                break;
            default:
                throw new IllegalStateException(String.format(
                        "The view type %d not supported by the ColorChainAdapter.", viewType));
        }

        // Create theme options
        Spinner timeSpinner = (Spinner) v.findViewById(R.id.time_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                R.array.time_items, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        timeSpinner.setAdapter(timeAdapter);
        // Set spinner position
        int spinnerPosition = timeAdapter.getPosition(delayToTimeSelection(defaultDelay));
        timeSpinner.setSelection(spinnerPosition);

        switch(viewType) {
            case COLOR_VIEW_TYPE:
                timeSpinner.setOnItemSelectedListener((ColorViewHolder) vh);
                break;
            case IMAGE_VIEW_TYPE:
                timeSpinner.setOnItemSelectedListener((ImageViewHolder) vh);
                break;
        }

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(holder instanceof ColorViewHolder) {
            ((ColorViewHolder) holder).colorView.setColor(colorChain.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position < colorChain.size()
                ? COLOR_VIEW_TYPE
                : IMAGE_VIEW_TYPE;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return colorChain.size() + 1; // +1 for the plus icon
    }

    public void add(int color) {
        colorChain.add(color);
        notifyItemInserted(colorChain.size()-1);
    }

    public void remove(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if(position < colorChain.size()) {
            colorChain.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setActivity(Activity activity){
        mActivity = activity;
    }

    public long timeSelectionToMillis(String selection){
        final String ms100 = mActivity.getString(R.string.ms100_string);
        final String ms250 = mActivity.getString(R.string.ms250_string);
        final String ms500 = mActivity.getString(R.string.ms500_string);
        final String ms1000 = mActivity.getString(R.string.ms1000_string);

        long result = 0;
        if(ms100.equals(selection)){
            result = 100;
        } else if(ms250.equals(selection)){
            result = 250;
        } else if(ms500.equals(selection)){
            result = 500;
        } else if(ms1000.equals(selection)){
            result = 1000;
        }

        return result;
    }

    public String delayToTimeSelection(long delay){
        String result = mActivity.getString(R.string.ms100_string);
        if(delay == 100){
            result = mActivity.getString(R.string.ms100_string);
        } else if(delay == 250){
            result = mActivity.getString(R.string.ms250_string);
        } else if(delay == 500){
            result = mActivity.getString(R.string.ms500_string);
        } else if(delay == 1000){
            result = mActivity.getString(R.string.ms1000_string);
        }

        return result;
    }

}