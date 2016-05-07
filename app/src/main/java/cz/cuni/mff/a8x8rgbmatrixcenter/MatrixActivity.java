package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class MatrixActivity extends AppCompatActivity {

    public static final String INTENT_CALLER_KEY = "CALLER";

    public static final String DRAWER_POSITION = "DRAWER_POSITION";
    public static final String LED_COLOR_KEY = "LED_COLOR";

    public static final String PREFS_NAME = "RGBMatrixCenterPrefs";
    public static final String THEME_SETTINGS_KEY = "THEME_ID";
    public static int currentTheme;

    private int drawerPosition = 0;
    private Fragment fragment;

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
        // If the state is restored from saved instance
        if(savedInstanceState != null) {
            drawerPosition = savedInstanceState.getInt(DRAWER_POSITION);
        }

        setFragment((String) drawerList.getItemAtPosition(drawerPosition), drawerPosition, savedInstanceState);
        drawerList.setItemChecked(drawerPosition, true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        final String matrixFragment = getString(R.string.matrix_string);

        ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        // Save current state
        savedInstanceState.putInt(DRAWER_POSITION, drawerPosition);
        if(matrixFragment.equals(drawerList.getItemAtPosition(drawerPosition))) {
            MatrixFragment mf = (MatrixFragment) fragment;
            mf.saveMatrixState(savedInstanceState);
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
            String caller = data.getStringExtra(INTENT_CALLER_KEY);
            if(ColorSelectionView.class.getName().equals(caller)){
                ColorSelectionView view = (ColorSelectionView) findViewById(R.id.colorSelectionView);
                view.onActivityResult(data);
            } else {
                throw new UnsupportedOperationException(String.format("The \"%s\" intent caller not supported.", caller));
            }

        }
    }

    public void setFragment(String fragmentName, int position, Bundle savedInstanceState){
        final String matrixFragment = getString(R.string.matrix_string);
        final String settingsFragment = getString(R.string.settings_string);
        final String aboutFragment = getString(R.string.about_string);

        if(matrixFragment.equals(fragmentName)) {
            MatrixFragment mf = new MatrixFragment();
            if(savedInstanceState != null) {
                int[] ledColors = savedInstanceState.getIntArray(LED_COLOR_KEY);
                mf.setLedColors(ledColors);
            }

            fragment = mf;
        } else if(settingsFragment.equals(fragmentName)) {
            fragment = new SettingsFragment();
        } else if(aboutFragment.equals(fragmentName)) {
            fragment = new AboutFragment();
        } else {
            throw new UnsupportedOperationException(String.format("The \"%s\" fragment in menu is not supported.", fragmentName));
        }

        // Insert the fragment by replacing any existing fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        drawerPosition = position;
    }
}
