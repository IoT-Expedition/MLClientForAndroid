package edu.cmu.hcii.giotto.android.mlfrontend.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.cmu.hcii.giotto.android.mlfrontend.R;
import edu.cmu.hcii.giotto.android.mlfrontend.application.GIoTTOApplication;
import edu.cmu.hcii.giotto.android.mlfrontend.application.SensorEntry;
import edu.cmu.hcii.giotto.android.mlfrontend.fragment.BdSettingFragment;
import edu.cmu.hcii.giotto.android.mlfrontend.fragment.LocationEmulationFragment;
import edu.cmu.hcii.giotto.android.mlfrontend.fragment.SensorListFragment;
import edu.cmu.hcii.giotto.android.mlfrontend.fragment.MlSettingFragment;
import edu.cmu.hcii.giotto.android.mlfrontend.helper.BuildingDepotHelper;

// Main Acitivty
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SensorListFragment mSensorListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((GIoTTOApplication)getApplication()).refreshBuildingDepotAccessToken();

        // Load content view
        setContentView(R.layout.activity_main);

        // Create an action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Create a drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create a sensor list
        FragmentManager manager = getSupportFragmentManager();
        mSensorListFragment = SensorListFragment.newInstance();
        manager.beginTransaction()
            .replace(R.id.main_frame, mSensorListFragment)
            .commit();
    }

    // When returning from another activity, refresh the sensor list
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSensorListFragment.refresh();
    }

    // When this activity is restarted, refresh the sensor list becasue the list could have been modified
    @Override
    protected void onRestart(){
        super.onRestart();
        mSensorListFragment.refresh();;

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // Handle menu selection in the drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "Navigation drawer selected item id = " + id);

        Fragment fragment = null;
        if (id == R.id.drawer_item_1) {
            fragment = SensorListFragment.newInstance();
        } else if (id == R.id.drawer_item_2){
            fragment = MlSettingFragment.newInstance();
        } else if (id == R.id.drawer_item_3){
            fragment = BdSettingFragment.newInstance();
        } else if (id == R.id.drawer_item_4){
            fragment = LocationEmulationFragment.newInstance();
        }
        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();

            item.setChecked(true);
            mDrawerLayout.closeDrawers();
        }

        return false;
    }
}
