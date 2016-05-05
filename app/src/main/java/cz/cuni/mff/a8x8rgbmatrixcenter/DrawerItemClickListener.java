package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Dominik Skoda on 05.05.2016.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Activity mActivity;

    public DrawerItemClickListener(Activity activity,  DrawerLayout drawerLayout, ListView drawerList){
        mActivity = activity;
        mDrawerLayout = drawerLayout;
        mDrawerList = drawerList;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {

        Fragment fragment;
        String item  = (String) mDrawerList.getItemAtPosition(position);
        switch (item) {
            case "Matrix":
                fragment = new MatrixFragment();
                break;
            case "Settings":
                fragment = new SettingsFragment();
                break;
            case "About":
                fragment = new AboutFragment();
                break;
            default:
                throw new UnsupportedOperationException(String.format("The \"%s\" item in menu is not supported.", item));
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = mActivity.getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

}