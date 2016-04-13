package edu.cmu.hcii.giotto.android.mlfrontend.application;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.hcii.giotto.android.mlfrontend.R;
import edu.cmu.hcii.giotto.android.mlfrontend.helper.BuildingDepotHelper;
import edu.cmu.hcii.giotto.android.mlfrontend.helper.LocationHelper;

/**
 * Created by ehayashi on 9/23/15.
 */
public class SensorEntry {
    public String _id;
    public String name;
    public String description;
    public List<String> labels;
    public String userId;
    public List<DataSampleEntry> trainingSet;
    public String sensorUuid;
    public List<String> inputs;

    public SensorEntry(Context context){
        this._id = "";
        this.name = "New Sensor";
        this.description = "";
        this.labels = new ArrayList<String>();
        this.userId = "default";
        this.trainingSet = new ArrayList<>();
        this.sensorUuid = "";
    }

    public SensorEntry(JSONObject obj){
        try {
            this._id = obj.getString("_id");
            this.name = obj.getString("name");
            this.description = obj.getString("description");
            this.labels = new ArrayList<String>();
            JSONArray array = obj.getJSONArray("labels");
            for(int i=0; i<array.length(); i++){
                String s = array.get(i).toString();
                this.labels.add(s);
            }
            this.userId = obj.getString("user_id");
            this.sensorUuid = obj.getString("sensor_uuid");
            this.inputs = new ArrayList<>();
            array = obj.getJSONArray("inputs");
            for(int i=0; i<array.length(); i++) {
                String s = array.get(i).toString();
                this.inputs.add(s);
            }
        } catch (Exception e){

        }
    }

    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        JSONArray labels = new JSONArray(this.labels);

        try {
            if(!this._id.equals("")) {
                obj.put("_id", this._id);
            }
            obj.put("name", this.name);
            obj.put("description", this.description);
            obj.put("labels", new JSONArray(this.labels));
            obj.put("user_id", this.userId);
            obj.put("sensor_uuid", this.sensorUuid);
            obj.put("inputs", new JSONArray(this.inputs));
            obj.put("training_set", new JSONArray(this.trainingSet));
        } catch (Exception e){

        }

        return obj;
    }
}
