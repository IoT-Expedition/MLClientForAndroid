package edu.cmu.hcii.giotto.android.mlfrontend.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import edu.cmu.hcii.giotto.android.mlfrontend.R;
import edu.cmu.hcii.giotto.android.mlfrontend.application.GIoTTOApplication;
import edu.cmu.hcii.giotto.android.mlfrontend.application.SensorEntry;
import edu.cmu.hcii.giotto.android.mlfrontend.helper.MachineLearningHelper;

/**
 * An activity where users can edit sensor information
 */
public class EditSensorActivity extends AppCompatActivity implements MachineLearningHelper.Callbacks {
    private SensorEntry sensorEntry;
    private ArrayList<TextView> labelViewList;
    private MachineLearningHelper machineLearningHelper;

    class LabelItem {
        private int index = -1;

        LabelItem(int idx) {
            index = idx;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sensor);

        labelViewList = new ArrayList<TextView>();  // ArrayList holding labels

        // Sensor ID has been passed from a previous activity
        final String sensorId = getIntent().getStringExtra("id");

        if(sensorId.equals("")){    // If a sensor ID has not been passed, it means creating a new sensor
            sensorEntry = new SensorEntry(getApplicationContext());
            sensorEntry.inputs = ((GIoTTOApplication)getApplication()).getNearbyDevices();
            loadSensorInformation(sensorEntry);
        }else{  // Otherwise, load sensor information. onSuccess will be called when the information is received.
            if(machineLearningHelper == null){
                machineLearningHelper = new MachineLearningHelper(getApplicationContext());
            }
            machineLearningHelper.setCallbacks(this);
            machineLearningHelper.getSensor(sensorId);
        }

        // Plus button
        ImageButton plusImageButton = (ImageButton)findViewById(R.id.edit_sensor_label_plus_button);
        plusImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add a blank label
                inflateEditItem("");
            }
        });

        // Save button
        View saveButton = (View)findViewById(R.id.edit_sensor_save_button);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Save sensor information and go back to the previous activity
                saveSensorInformation();
            }
        });

        // Cancel button
        View cancelButton = (View) findViewById(R.id.edit_sensor_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to previous activity without saving sensor information
                finish();
            }
        });
    }

    // Set the sensor information to UIs
    private void loadSensorInformation(SensorEntry sensorEntry){
        TextView sensorName = (TextView)findViewById(R.id.edit_sensor_name_edittext);
        TextView sensorInfo = (TextView)findViewById(R.id.edit_sensor_info_edittext);

        sensorName.setText(sensorEntry.name);
        sensorInfo.setText(sensorEntry.description);

        for(String label:sensorEntry.labels){
            inflateEditItem(label);
        }
    }

    // Save sensor information to a machine learning server
    private void saveSensorInformation(){
        TextView nameView = (TextView)findViewById(R.id.edit_sensor_name_edittext);
        TextView infoView = (TextView)findViewById(R.id.edit_sensor_info_edittext);

        // Retrieve sensor information from UIs
        sensorEntry.name = nameView.getText().toString();
        sensorEntry.description = infoView.getText().toString();

        sensorEntry.labels.clear();
        for(int i=0; i<labelViewList.size(); i++){
            sensorEntry.labels.add(labelViewList.get(i).getText().toString());
        }

        // Save the information to a machine learning server
        if(machineLearningHelper == null) {
            machineLearningHelper = new MachineLearningHelper(getApplicationContext());
            machineLearningHelper.setCallbacks(this);
        }
        if(sensorEntry._id.equals("")) {
            machineLearningHelper.createSensor(sensorEntry);
        }else{
            machineLearningHelper.updateSensor(sensorEntry);
        }
    }

    // Create a label row
    private void inflateEditItem(String label) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.list_item_label, null);

        EditText editText = (EditText)itemView.findViewById(R.id.list_item_label_edittext);
        editText.setText(label, TextView.BufferType.EDITABLE);

        LinearLayout containerView = (LinearLayout) findViewById(R.id.edit_sensor_labels_container);
        int index = containerView.getChildCount()-1;
        LabelItem labelItem = new LabelItem(index);

        ImageButton button = (ImageButton)itemView.findViewById(R.id.list_item_label_minus_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove editText for a list
                EditText edit = (EditText)((ViewGroup)v.getParent()).getChildAt(0);
                int index = labelViewList.indexOf(edit);
                labelViewList.remove(index);
                sensorEntry.labels.remove(index);

                // remove a row
                ((ViewGroup)v.getParent().getParent()).removeView((ViewGroup)v.getParent());
            }
        });

        labelViewList.add(editText);
        containerView.addView(itemView, containerView.getChildCount());
    }

    // This function is called in response to machineLearningHelper.getSensor(sensorId) on onCreate
    public void onSuccess(JSONObject response){
        try{
            if(response.getString("method").equals("GET")) {
                JSONObject obj = response.getJSONObject("ret");
                sensorEntry = new SensorEntry(obj);
                loadSensorInformation(sensorEntry);
            } else if(response.getString("method").equals("POST")){
                setResult(RESULT_OK);
                finish();
            }
        } catch (Exception e){
            System.err.println("Error in loading JSON: " + e.getMessage());
        }
    }

    // This function is called when machineLearningHelper.getSensor(sensorId) returns an error
    public void onError(String response){
        System.err.println("Error in calling a machine learning server;" + response);
    }
}
