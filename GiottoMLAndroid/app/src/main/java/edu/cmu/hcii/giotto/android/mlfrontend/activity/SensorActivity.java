package edu.cmu.hcii.giotto.android.mlfrontend.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import edu.cmu.hcii.giotto.android.mlfrontend.R;
import edu.cmu.hcii.giotto.android.mlfrontend.application.SensorEntry;
import edu.cmu.hcii.giotto.android.mlfrontend.helper.MachineLearningHelper;

public class SensorActivity extends AppCompatActivity implements View.OnClickListener, MachineLearningHelper.Callbacks{

    private SensorEntry sensorEntry;
    private MachineLearningHelper machineLearningHelper;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When returning from another activity, refresh the sensor information
        if(resultCode==RESULT_OK) {
            // Refresh contents
            if (machineLearningHelper == null) {
                machineLearningHelper = new MachineLearningHelper(getApplicationContext());
                machineLearningHelper.setCallbacks(this);
            }
            machineLearningHelper.getSensor(sensorEntry._id);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load contents
        setContentView(R.layout.activity_sensor);

        // Create an action bar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // A previous activity has passed a sensor ID
        Intent intent = getIntent();
        String sensorId = intent.getStringExtra("id");

        // Load the sensor information using the sensor ID
        machineLearningHelper = new MachineLearningHelper(getApplicationContext());
        machineLearningHelper.setCallbacks(this);
        machineLearningHelper.getSensor(sensorId);  // onSuccess will be called when receiving the sensor information

        // Create a floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.sensor_floatingActionButton);
        fab.setOnClickListener(this);
    }

    // Set sensor information to UIs
    private void loadSensorInformation(SensorEntry sensorEntry){
        TextView textView = (TextView)findViewById(R.id.sensor_info_exit_text);
        textView.setText(sensorEntry.description);

        inflateStatusItems(sensorEntry.labels);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(sensorEntry.name);

        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
    }

    // Training button clicked
    public void onClick(View view){
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra("id", sensorEntry._id);
        startActivityForResult(intent, 1);
    }

    // The menu button at the top right corner clicked
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor, menu);
        return true;
    }

    // A menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, EditSensorActivity.class);
            intent.putExtra("id", sensorEntry._id);
            startActivityForResult(intent, 1);
        }else if(id == R.id.action_delete){
            if(machineLearningHelper == null){
                machineLearningHelper = new MachineLearningHelper(getApplicationContext());
                machineLearningHelper.setCallbacks(this);
            }
            machineLearningHelper.deleteSensor(sensorEntry._id);
        }

        return super.onOptionsItemSelected(item);
    }

    private void inflateStatusItems(List<String> list) {
        LinearLayout containerView = (LinearLayout) findViewById(R.id.sensor_status_container);
        containerView.removeAllViews();

        if(list != null) {
            for (String label : list) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View itemView = inflater.inflate(R.layout.list_item_status, null);

                TextView textView = (TextView)itemView.findViewById(R.id.list_item_status_textview);
                textView.setText(label);

                containerView.addView(itemView, containerView.getChildCount());
            }
        }
    }

    // Called when the activity gets response from a machine learning server
    public void onSuccess(JSONObject response){
        try {
            if(response.getString("url").contains("/sensor") && response.getString("method").equals("GET")) {
                JSONObject sensorObj = response.getJSONObject("ret");
                sensorEntry = new SensorEntry(sensorObj);
                loadSensorInformation(sensorEntry);
            } else if(response.getString("method").equals("DELETE")){
                finish();
            }
        } catch (Exception e){
            System.err.println("Error in parsing JSON: " + e.getMessage());
        }
    }

    // Called when the activity gets an error from a machine learning server
    public void onError(String response){
        System.err.println("Error in calling a machine learning server " + response);
    }
}
