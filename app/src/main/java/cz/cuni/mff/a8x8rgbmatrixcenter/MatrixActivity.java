package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;

import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.BT_COMMAND_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.BT_DEVICE_MAC_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.REQUEST_CONNECT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.BluetoothService.REQUEST_DISCONNECT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.ColorSelectionView.COLOR_KEY;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class MatrixActivity extends AppCompatActivity {

    public static final String DRAWER_POSITION_KEY = "DRAWER_POSITION";
    public static final String LED_INDEX_KEY = "LED_INDEX";
    public static final String LED_COLOR_KEY = "LED_COLOR";
    public static final String MESSAGE_KEY = "MESSAGE";

    public static final int REQUEST_COLOR_SELECT = 1;
    public static final int REQUEST_NEW_COLOR = 2;
    public static final int REQUEST_ENABLE_BT = 3;

    public static final String PREFS_NAME = "RGBMatrixCenterPrefs";
    public static final String THEME_SETTINGS_KEY = "THEME_ID";
    public static int currentTheme;

    private int drawerPosition = 0;
    private Fragment fragment;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice connectedDevice;
    private final BroadcastReceiver mBTBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(fragment instanceof SettingsFragment){
                            SettingsFragment sf = (SettingsFragment) fragment;
                            sf.disableBTSpinner();
                         } else if(fragment instanceof MatrixFragment){
                            MatrixFragment mf = (MatrixFragment) fragment;
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if(fragment instanceof SettingsFragment){
                            SettingsFragment sf = (SettingsFragment) fragment;
                            sf.fillBTDevices();
                        } else if(fragment instanceof MatrixFragment){
                            MatrixFragment mf = (MatrixFragment) fragment;
                        }
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver timedColorBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(fragment instanceof SwipeFragment){
                SwipeFragment sf = (SwipeFragment) fragment;
                int ledIndex = intent.getIntExtra(LED_INDEX_KEY, 0);
                int ledColor = intent.getIntExtra(LED_COLOR_KEY, Color.BLACK);
                sf.setLedColor(ledIndex, ledColor);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        currentTheme = settings.getInt(THEME_SETTINGS_KEY, R.style.AppTheme);
        setTheme(currentTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.matrix_activity_layout);

        // Initialize drawer list
        String[] menuItems = getResources().getStringArray(R.array.menu_items);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, menuItems));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener(this, drawerLayout, drawerList));
        // Select current fragment
        drawerPosition = 0;

        // Initialize bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the state is restored from saved instance
        if(savedInstanceState != null) {
            // Retrieve drawer position
            drawerPosition = savedInstanceState.getInt(DRAWER_POSITION_KEY);

            // Retrieve connected deviec
            String connectedDeviceMAC = savedInstanceState.getString(BluetoothService.BT_DEVICE_MAC_KEY, null);
            if(connectedDeviceMAC != null
                    && mBluetoothAdapter != null
                    && mBluetoothAdapter.isEnabled()){
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                for(BluetoothDevice device : pairedDevices){
                    if(device.getAddress().equals(connectedDeviceMAC)){
                        connectedDevice = device;
                    }
                }
            }
        }

        // Set current fragment
        setFragment((String) drawerList.getItemAtPosition(drawerPosition), drawerPosition, savedInstanceState);
        drawerList.setItemChecked(drawerPosition, true);

        // Register BT state change broadcast receiver
        IntentFilter btFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBTBroadcastReceiver, btFilter);

        // Register color broadcast receiver
        IntentFilter colorFilter = new IntentFilter(TimedColorService.COLOR_TIMEUP);
        LocalBroadcastManager.getInstance(this).registerReceiver(timedColorBroadcastReceiver, colorFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister broadcast receivers
        unregisterReceiver(mBTBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timedColorBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        final String matrixFragment = getString(R.string.matrix_string);

        ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        // Save current state
        savedInstanceState.putInt(DRAWER_POSITION_KEY, drawerPosition);
        if(matrixFragment.equals(drawerList.getItemAtPosition(drawerPosition))) {
            MatrixFragment mf = (MatrixFragment) fragment;
            mf.saveMatrixState(savedInstanceState);
        }
        if(connectedDevice != null) {
            savedInstanceState.putString(BluetoothService.BT_DEVICE_MAC_KEY, connectedDevice.getAddress());
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            switch(requestCode) {
                case REQUEST_COLOR_SELECT:
                    ColorSelectionView view = (ColorSelectionView) findViewById(R.id.colorSelectionView);
                    view.onActivityResult(data);
                    break;
                case REQUEST_NEW_COLOR: {
                    SwipeFragment sf = (SwipeFragment) fragment;
                    sf.addColor(data.getIntExtra(COLOR_KEY, 0));
                    break;
                }
                case REQUEST_ENABLE_BT: {
                    SettingsFragment sf = (SettingsFragment) fragment;
                    sf.fillBTDevices();
                    break;
                }
                default:
                    throw new UnsupportedOperationException(String.format("The \"%d\" requestCode not supported.", requestCode));
            }
        }
    }

    public void setFragment(String fragmentName, int position, Bundle savedInstanceState){
        final String matrixFragment = getString(R.string.matrix_string);
        final String swipeFragment = getString(R.string.swipe_string);
        final String settingsFragment = getString(R.string.settings_string);
        final String aboutFragment = getString(R.string.about_string);

        if(matrixFragment.equals(fragmentName)) {
            MatrixFragment mf = (MatrixFragment) getFragmentManager().findFragmentByTag(fragmentName);
            if(mf == null) {
                mf = new MatrixFragment();
            }
            if (savedInstanceState != null) {
                int[] ledColors = savedInstanceState.getIntArray(LED_COLOR_KEY);
                mf.setLedColors(ledColors);
            }
            mf.setActivity(this);
            fragment = mf;
        } else if(swipeFragment.equals(fragmentName)){
            SwipeFragment sf = (SwipeFragment) getFragmentManager().findFragmentByTag(fragmentName);
            if(sf == null) {
                sf = new SwipeFragment();
            }
            sf.setActivity(this);
            fragment = sf;
        } else if(settingsFragment.equals(fragmentName)) {
            SettingsFragment sf = (SettingsFragment) getFragmentManager().findFragmentByTag(fragmentName);
            if(sf == null) {
                sf = new SettingsFragment();
            }
            sf.setActivity(this);
            fragment = sf;
        } else if(aboutFragment.equals(fragmentName)) {
            fragment = getFragmentManager().findFragmentByTag(fragmentName);
            if(fragment == null) {
                fragment = new AboutFragment();
            }
        } else {
            throw new UnsupportedOperationException(String.format("The \"%s\" fragment in menu is not supported.", fragmentName));
        }

        // Insert the fragment by replacing any existing fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, fragmentName)
                .commit();
        drawerPosition = position;
    }

    public BluetoothAdapter getBTAdapter(){
        return mBluetoothAdapter;
    }

    public void setConnectedDevice(BluetoothDevice device){
        if(connectedDevice != null){
            Intent intent = new Intent(this, BluetoothService.class);
            intent.putExtra(BT_COMMAND_KEY, REQUEST_DISCONNECT);
            startService(intent);
        }

        connectedDevice = device;

        if(connectedDevice != null) {
            Intent intent = new Intent(this, BluetoothService.class);
            intent.putExtra(BT_COMMAND_KEY, REQUEST_CONNECT);
            intent.putExtra(BT_DEVICE_MAC_KEY, connectedDevice.getAddress());
            startService(intent);
        }
    }

    public BluetoothDevice getConnectedDevice(){
        return connectedDevice;
    }
}
