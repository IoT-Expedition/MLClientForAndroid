package edu.cmu.hcii.giotto.android.mlfrontend.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.hcii.giotto.android.mlfrontend.application.SensorEntry;
import edu.cmu.hcii.giotto.android.mlfrontend.application.TrainingSampleEntry;

/**
 * Created by ehayashi on 2/28/16.
 */
public class BuildingDepotHelper {
    private Callbacks _callbacks = null;
    private RequestQueue _queue;
    private Context _context;
    private String _baseUrl;
    private String _apiPrefix;
    private String _accessToken;
    private String _authUrl;

    public BuildingDepotHelper (Context context){

        _context = context;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);

        String bdServer = prefs.getString("bd_server", "");
        String bdPort = prefs.getString("bd_port", "");
        _apiPrefix = prefs.getString("bd_api_prefix", "");
        _authUrl= bdServer + ":81";
        _baseUrl = bdServer + ":" + bdPort;
    }

    public interface Callbacks {
        public void onSuccess(JSONObject response);
        public void onError(String response);
    }

    public void setAccessToken(String accessToken){
        _accessToken = accessToken;
    }

    public void setCallbacks(Callbacks callbacks){
        _callbacks = callbacks;
    }

    public void getAccessToken(String id, String key){
        queryUrl("GET", _authUrl + "/oauth/access_token/client_id=" + id + "/client_secret=" + key);
    }


    public void sensorsAt(String location)  {
        //queryUrl("GET", _baseUrl + _apiPrefix + "/sensor/list?filter=metadata&location="+location);
        //queryUrlWithJson("POST", _baseUrl + "sensor", sensorEntry.toJson());
        String newjson = "{\"data\":{\"Tags\":[\"location:"+location+"\"]}}";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(newjson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String searchurl=_authUrl+"/api/search";
        Log.d(searchurl, "SearchURL: ");
        queryUrlWithJson("POST",searchurl, jsonObject);


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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if( _accessToken == null || _accessToken == ""){
                    return headers;
                }
                Map<String, String> newHeaders = new HashMap<String, String>();
                newHeaders.putAll(headers);
                newHeaders.put("Authorization", "Bearer " + _accessToken);
                return newHeaders;
            }
        };

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        _queue.add(jsonRequest);

    }
}
