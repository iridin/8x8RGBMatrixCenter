package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

/**
 * Created by Dominik Skoda on 06.05.2016.
 */
public class BTSpinnerListener implements AdapterView.OnItemSelectedListener {

    private MatrixActivity mActivity;
    List<BluetoothDevice> mDevices;

    public BTSpinnerListener(MatrixActivity activity, List<BluetoothDevice> devices){
        mActivity = activity;
        mDevices = devices;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(mActivity != null && position < mDevices.size()){
            mActivity.setConnectedDevice(mDevices.get(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}
