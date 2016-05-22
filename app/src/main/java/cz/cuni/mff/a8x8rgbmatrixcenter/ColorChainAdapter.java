package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Dominik Skoda on 11.05.2016.
 */
public class ColorChainAdapter extends RecyclerView.Adapter<ColorChainAdapter.ColorViewHolder> {

    public static final String COLOR_CHAIN_KEY = "COLOR_CHAIN";
    public static final String DELAY_CHAIN_KEY = "DELAY_CHAIN";


    private ArrayList<Integer> colorChain;
    private ArrayList<Long> delays;
    private long defaultDelay = 100;
    private Activity mActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ColorViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

        private ColorChainAdapter parent;

        public ColorView colorView;
        public int index;

        public ColorViewHolder(View colorView, ColorChainAdapter parent) {
            super(colorView);
            this.colorView = (ColorView) colorView.findViewById(R.id.color_view);
            this.parent = parent;
            index = parent.colorChain.size()-1;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
            final String selection = (String) adapter.getItemAtPosition(position);
            parent.delays.set(index, parent.timeSelectionToMillis(selection));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing
        }
    }

    // Provide a suitable constructor
    public ColorChainAdapter(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            colorChain = (ArrayList<Integer>) savedInstanceState.getSerializable(COLOR_CHAIN_KEY);
            delays = (ArrayList<Long>) savedInstanceState.getSerializable(DELAY_CHAIN_KEY);
            if (colorChain != null) {
                // Exit on successful recovery
                return;
            }
        }

        colorChain = new ArrayList<>();
        delays = new ArrayList<>();

        // Add default colors to the chain
        colorChain.add(Color.RED);
        delays.add(defaultDelay);
        colorChain.add(Color.BLACK);
        delays.add(defaultDelay);
    }


    public void saveInstanceState(Bundle savedInstanceState) {
        // Save current state
        savedInstanceState.putSerializable(COLOR_CHAIN_KEY, colorChain);
        savedInstanceState.putSerializable(DELAY_CHAIN_KEY, delays);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.color_time_layout, parent, false);
        ColorViewHolder vh = new ColorViewHolder(v, this);

        // Create time options
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

        timeSpinner.setOnItemSelectedListener((ColorViewHolder) vh);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ColorViewHolder cvh, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        cvh.colorView.setColor(colorChain.get(position));
        cvh.index = position;
        Spinner timeSpinner = (Spinner)cvh.itemView.findViewById(R.id.time_spinner);
        int spinnerPosition = ((ArrayAdapter<CharSequence>) timeSpinner.getAdapter())
                .getPosition(delayToTimeSelection(delays.get(position)));
        timeSpinner.setSelection(spinnerPosition);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return colorChain.size();
    }

    public void add(int color) {
        colorChain.add(color);
        delays.add(defaultDelay);
        notifyItemInserted(colorChain.size()-1);
    }

    public void remove(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if(position < colorChain.size()) {
            colorChain.remove(position);
            delays.remove(position);
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

    public ArrayList<TimedColor> getColorChain(int ledIndex){
        ArrayList<TimedColor> timedColorChain = new ArrayList<>();
        Long time = Calendar.getInstance().getTimeInMillis();
        for(int i = 0; i < colorChain.size(); i++){
            time += delays.get(i);
            timedColorChain.add(new TimedColor(ledIndex, colorChain.get(i), time));
        }
        return timedColorChain;
    }

}