package edu.cmu.hcii.giotto.android.mlfrontend.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.cmu.hcii.giotto.android.mlfrontend.application.SensorEntry;
import edu.cmu.hcii.giotto.android.mlfrontend.application.TrainingSampleEntry;

/**
 * Created by ehayashi on 10/29/15.
 */
public class MachineLearningHelper {
    private Callbacks _callbacks = null;
    private RequestQueue _queue;
    private Context _context;
    private String _baseUrl;

    public MachineLearningHelper(Context context) {
        _context = context;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);

        String mlServer = prefs.getString("ml_server", "");
        String mlPort = prefs.getString("ml_port", "");
        _baseUrl = mlServer + ":" + mlPort + "/";
    }

    public interface Callbacks {
        public void onSuccess(JSONObject response);
        public void onError(String response);
    }

    public void setCallbacks(Callbacks callbacks){
        _callbacks = callbacks;
    }

    public void getTime(){
        queryUrlWithJson("GET", _baseUrl + "time", null);
    }

    public void getSensors(){
        queryUrlWithJson("GET", _baseUrl + "sensors/", null);
    }

    public void createSensor(SensorEntry sensorEntry){
        queryUrlWithJson("POST", _baseUrl + "sensor", sensorEntry.toJson());
    }

    public void updateSensor(SensorEntry sensorEntry){
        queryUrlWithJson("PUT", _baseUrl + "sensor/" + sensorEntry._id, sensorEntry.toJson());
    }

    public void deleteSensor(String sensorId){
        queryUrl("DELETE", _baseUrl + "sensor/" + sensorId);
    }

    public void insertSampling(TrainingSampleEntry trainingSampleEntry){
        JSONObject json = trainingSampleEntry.toJson();
        Log.v("ML",json.toString());
        queryUrlWithJson("POST", _baseUrl + "sensor/" + trainingSampleEntry.sensorId + "/sample", json);
    }

    public void train(String sensorId){
        queryUrl("POST", _baseUrl + "sensor/" + sensorId + "/classifier/train");
    }

    public String crossValidation(long sensorId){
        String json = null;

        return json;
    }

    public void predict(String sensorId){
        queryUrl("GET", _baseUrl + "sensor/" + sensorId + "/classifier/predict");
    }

    public String stopSampling(){
        String json = null;

        return json;
    }

    public void getSensor(String sensorId){
        queryUrl("GET", _baseUrl + "sensor/" + sensorId);
    }

    public void queryUrl(String method, String url){
        queryUrlWithJson(method, url, null);
    }

    public void queryUrlWithJson(String method, String url, JSONObject jsonObject){
        if(_queue == null) {
            _queue = Volley.newRequestQueue(_context);
        }

        int httpMethod;

        if(method.equals("POST")){
            httpMethod = Request.Method.POST;
        } else if(method.equals("DELETE")){
            httpMethod = Request.Method.DELETE;
        } else if(method.equals("PUT")){
            httpMethod = Request.Method.PUT;
        } else {
            httpMethod = Request.Method.GET;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(httpMethod, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject obj) {
                    if(_callbacks != null){
                        _callbacks.onSuccess(obj);

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (_callbacks != null) {
                        _callbacks.onError(error.toString());
                    }
                }

        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        _queue.add(jsonRequest);

    }
}
