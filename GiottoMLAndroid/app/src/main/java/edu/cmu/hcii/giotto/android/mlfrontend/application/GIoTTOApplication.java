package edu.cmu.hcii.giotto.android.mlfrontend.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import com.squareup.leakcanary.LeakCanary;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.cmu.hcii.giotto.android.mlfrontend.R;
import edu.cmu.hcii.giotto.android.mlfrontend.helper.BuildingDepotHelper;

public class GIoTTOApplication extends Application implements BuildingDepotHelper.Callbacks {

    BuildingDepotHelper _buildingDepotHlper;
    ArrayList<String> _nearbyDevices;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        _nearbyDevices = new ArrayList<String>();
    }

    public ArrayList<String> getNearbyDevices(){
        return _nearbyDevices;
    }

    public void refreshNearbySensors(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        _buildingDepotHlper.sensorsAt(prefs.getString("location_emulation", ""));
    }

    public void refreshBuildingDepotAccessToken(){
        if(_buildingDepotHlper == null) {
            _buildingDepotHlper = new BuildingDepotHelper(getApplicationContext());
            _buildingDepotHlper.setCallbacks(this);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        _buildingDepotHlper.getAccessToken(prefs.getString("oauth_id", ""), prefs.getString("oauth_key", ""));
    }

    private void parseSensors(JSONArray array){
        try {
            _nearbyDevices = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                String uuid = obj.getString("name");

                _nearbyDevices.add(uuid);
            }
        }
        catch(Exception e){
        }
    }

    public void onSuccess(JSONObject response) {
        try {
            if(response.has("access_token")) {
                String accessToken = response.getString("access_token");
                _buildingDepotHlper.setAccessToken(accessToken);
                refreshNearbySensors();
            }else if(response.has("data")){
                JSONArray array = response.getJSONArray("result");
                parseSensors(array);
            }
            else if (response.has("result")){
                JSONArray array = response.getJSONArray("result");
                parseSensors(array);
            }
        }
        catch(Exception e){

        }
    }

    public void onError(String response){
        Log.v("BD", "ERROR");
    }
}
