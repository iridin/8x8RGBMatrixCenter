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

    public static final String INTENT_CALLER = "CALLER";

    public static final String PREFS_NAME = "RGBMatrixCenterPrefs";
    public static final String THEME_SETTINGS = "THEME_ID";
    public static int currentTheme;

    private static int drawerPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        currentTheme = settings.getInt(THEME_SETTINGS, R.style.AppTheme);
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
        if(drawerPosition == -1){
            drawerPosition = 0;
        }
        setFragment((String) drawerList.getItemAtPosition(drawerPosition), drawerPosition);
        drawerList.setItemChecked(drawerPosition, true);
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
            String caller = data.getStringExtra(INTENT_CALLER);
            if(ColorSelectionView.class.getName().equals(caller)){
                ColorSelectionView view = (ColorSelectionView) findViewById(R.id.colorSelectionView);
                view.onActivityResult(data);
            } else {
                throw new UnsupportedOperationException(String.format("The \"%s\" intent caller not supported.", caller));
            }

        }
    }

    public void setFragment(String fragmentName, int position){
        final String matrixFragment = getString(R.string.matrix_string);
        final String settingsFragment = getString(R.string.settings_string);
        final String aboutFragment = getString(R.string.about_string);

        Fragment fragment;
        if(matrixFragment.equals(fragmentName)) {
            fragment = new MatrixFragment();
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
