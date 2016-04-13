package edu.cmu.hcii.giotto.android.mlfrontend.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ehayashi on 4/2/16.
 */
public class LocationHelper {

    private Context _context;

    public LocationHelper(Context context){
        _context = context;
    }

    public String currentLocation(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        return prefs.getString("location_emulation","");
    }
}
