package edu.cmu.hcii.giotto.android.mlfrontend.application;

import org.json.JSONObject;

/**
 * Created by ehayashi on 2/1/16.
 */
public class TrainingSampleEntry {
    public double startTime;
    public double endTime;
    public String sensorId;
    public String label;
    public String userId;
    public String _id;

    public JSONObject toJson(){
        JSONObject obj = new JSONObject();

        try {
            obj.put("_id", this._id);
            obj.put("start_time", this.startTime);
            obj.put("end_time", this.endTime);
            obj.put("user_id", this.userId);
            obj.put("sensor_id", this.sensorId);
            obj.put("label", this.label);
        } catch (Exception e){

        }

        return obj;
    }
}
