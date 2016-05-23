package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.BT_COMMAND_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.BT_TIMEOUT_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.REQUEST_CHANGE_TIMEOUT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.REQUEST_ENABLE_BT;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class SettingsFragment extends Fragment {

    final String[] supportedDevices = new String[]{ "HC-05" };

    public static final int MIN_TIMEOUT = 20; // in seconds
    public static final int MAX_TIMEOUT = 600; // in seconds
    public static final int TIMEOUT_STEP_10 = 10; // in seconds
    public static final int TIMEOUT_STEP_30 = 30; // in seconds

    MatrixActivity mActivity;
    List<BluetoothDevice> devices;
    Spinner btSpinner;


    public SettingsFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_layout, container, false);
        mActivity = (MatrixActivity) getActivity();

        registerThemeSpinner(rootView);
        registerBluetoothSpinner(rootView);
        registerTimeoutPicker(rootView);

        return rootView;
    }

    private void registerThemeSpinner(View rootView){
        Activity activity = getActivity();

        // Create theme options
        Spinner themeSpinner = (Spinner) rootView.findViewById(R.id.theme_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(activity,
                R.array.theme_items, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        themeSpinner.setAdapter(themeAdapter);

        ThemeSpinnerListener themeSpinnerListener = new ThemeSpinnerListener(activity);
        themeSpinner.setOnItemSelectedListener(themeSpinnerListener);

        // Set selected value
        String themeName = themeSpinnerListener.themeToString(MatrixActivity.currentTheme);
        int spinnerPosition = themeAdapter.getPosition(themeName);
        themeSpinner.setSelection(spinnerPosition);
    }

    private void registerBluetoothSpinner(View rootView){
        // Create Bluetooth spinner
        btSpinner = (Spinner) rootView.findViewById(R.id.bt_device_spinner);

        if(mActivity != null) {
            BluetoothAdapter btAdapter = mActivity.getBTAdapter();
            if (btAdapter == null) {
                // Device does not support Bluetooth
                disableBTSpinner();
            } else {
                // Check if BT is active
                if (!btAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    disableBTSpinner();
                } else {
                    fillBTDevices();
                }
            }
        } else {
            disableBTSpinner();
        }
    }

    private void registerTimeoutPicker(View rootView){
        // Create timeout picker
        final NumberPicker timePicker = (NumberPicker) rootView.findViewById(R.id.bt_device_timeout_picker);
        timePicker.setMinValue(MIN_TIMEOUT);
        timePicker.setMaxValue(MAX_TIMEOUT);
        timePicker.setValue((int) mActivity.getDeviceDisconnectTimeout() / 1000); // convert from millis
        timePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mActivity.setDeviceDisconnectTimeout(newVal * 1000); // convert to milliseconds
            }
        });

        // Create timeout buttons
        Button.OnClickListener timeoutButtonListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int timeout = (int) mActivity.getDeviceDisconnectTimeout() / 1000; // convert from millis
                int newVal;
                switch (v.getId()){
                    case R.id.bt_device_timeout_m10:
                        newVal = Math.max(MIN_TIMEOUT, timeout - TIMEOUT_STEP_10);
                        break;
                    case R.id.bt_device_timeout_m30:
                        newVal = Math.max(MIN_TIMEOUT, timeout - TIMEOUT_STEP_30);
                        break;
                    case R.id.bt_device_timeout_p10:
                        newVal = Math.min(MAX_TIMEOUT, timeout + TIMEOUT_STEP_10);
                        break;
                    case R.id.bt_device_timeout_p30:
                        newVal = Math.min(MAX_TIMEOUT, timeout + TIMEOUT_STEP_30);
                        break;
                    default:
                        throw new IllegalStateException(String.format(
                                "The id \"%d\" not supported by the timeoutButtonListener",
                                v.getId()));
                }

                mActivity.setDeviceDisconnectTimeout(newVal * 1000); // convert to milliseconds
                timePicker.setValue(newVal);
                timePicker.invalidate();
            }
        };

        Button m10Button = (Button) rootView.findViewById(R.id.bt_device_timeout_m10);
        m10Button.setOnClickListener(timeoutButtonListener);
        Button m30Button = (Button) rootView.findViewById(R.id.bt_device_timeout_m30);
        m30Button.setOnClickListener(timeoutButtonListener);
        Button p10Button = (Button) rootView.findViewById(R.id.bt_device_timeout_p10);
        p10Button.setOnClickListener(timeoutButtonListener);
        Button p30Button = (Button) rootView.findViewById(R.id.bt_device_timeout_p30);
        p30Button.setOnClickListener(timeoutButtonListener);
    }

    public void disableBTSpinner(){
        if(btSpinner == null){
            return;
        }
        ArrayAdapter<CharSequence> btSpinnerAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.bt_default_item,
                android.R.layout.simple_spinner_item);
        btSpinner.setAdapter(btSpinnerAdapter);
        btSpinner.setEnabled(false);
        btSpinner.invalidate();
    }

    private boolean isBTDeviceSupported(BluetoothDevice device){
        boolean supported = false;
        for(String devName : supportedDevices){
            if(devName.equals(device.getName())){
                supported = true;
            }
        }
        return supported;
    }

    public void fillBTDevices(){
        if(btSpinner == null){
            return;
        }

        BluetoothAdapter btAdapter = mActivity.getBTAdapter();
        // Create BT options
        List<String> deviceNames = new ArrayList<>();
        devices = new ArrayList<>();
        deviceNames.add("No Device");
        devices.add(null); // Corresponds to "No Device"
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        // Loop through paired devices
        for (BluetoothDevice device : pairedDevices) {
            if(isBTDeviceSupported(device)) {
                // Add the name and address to an array adapter to show in a ListView
                devices.add(device);
                deviceNames.add(device.getName() + "\n\t" + device.getAddress());
            }
        }


        ArrayAdapter btArrayAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, deviceNames);
        btArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        btSpinner.setAdapter(btArrayAdapter);

        int spinnerPosition = devices.indexOf(mActivity.getConnectedDevice());
        btSpinner.setSelection(spinnerPosition);

        BTSpinnerListener btSpinnerListener = new BTSpinnerListener(mActivity, devices);
        btSpinner.setOnItemSelectedListener(btSpinnerListener);

        btSpinner.setEnabled(true);
        btSpinner.invalidate();
    }

    public void refreshConnectedDevice(){
        int spinnerPosition = devices.indexOf(mActivity.getConnectedDevice());
        btSpinner.setSelection(spinnerPosition);
        btSpinner.invalidate();
    }

}

