package cz.cuni.mff.a8x8rgbmatrixcenter;

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
    private MatrixActivity mActivity;

    public DrawerItemClickListener(MatrixActivity activity,  DrawerLayout drawerLayout, ListView drawerList){
        mActivity = activity;
        mDrawerLayout = drawerLayout;
        mDrawerList = drawerList;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {

        String item  = (String) mDrawerList.getItemAtPosition(position);
        mActivity.setFragment(item, position, null);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

}