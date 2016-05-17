package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.REQUEST_NEW_COLOR;
import static cz.cuni.mff.a8x8rgbmatrixcenter.ColorSelectionView.COLOR_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.ColorSelectionView.COLOR_VIEW_INDEX_KEY;


/**
 * Created by Dominik Skoda on 11.05.2016.
 */
public class ColorChainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int COLOR_VIEW_TYPE = 0;
    public static final int IMAGE_VIEW_TYPE = 1;

    private List<Integer> colorChain;
    private Activity mActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ColorView colorView;
        public ColorViewHolder(View colorView) {
            super(colorView);
            this.colorView = (ColorView) colorView.findViewById(R.id.color_view);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Activity mActivity;

        public ImageViewHolder(View imageView){
            super(imageView);
            imageView.setOnClickListener(this);
        }

        public void setActivity(Activity activity){
            mActivity = activity;
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
                vh = new ColorViewHolder(v);
                break;
            case IMAGE_VIEW_TYPE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.image_time_layout, parent, false);
                ImageViewHolder ivh = new ImageViewHolder(v);
                ivh.setActivity(mActivity);
                vh = ivh;
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

        /*ThemeSpinnerListener themeSpinnerListener = new ThemeSpinnerListener(parent.getContext());
        timeSpinner.setOnItemSelectedListener(timeSpinnerListener);*/

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

}