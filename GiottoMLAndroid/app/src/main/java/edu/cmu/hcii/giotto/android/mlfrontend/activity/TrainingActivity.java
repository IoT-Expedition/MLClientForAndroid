package edu.cmu.hcii.giotto.android.mlfrontend.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.hcii.giotto.android.mlfrontend.R;
import edu.cmu.hcii.giotto.android.mlfrontend.application.SensorEntry;
import edu.cmu.hcii.giotto.android.mlfrontend.application.TrainingSampleEntry;
import edu.cmu.hcii.giotto.android.mlfrontend.helper.MachineLearningHelper;

/**
 * Created by ehayashi on 10/19/15.
 */
public class TrainingActivity extends AppCompatActivity implements View.OnClickListener, MachineLearningHelper.Callbacks {

    private SensorEntry sensorEntry = null;
    private MachineLearningHelper machineLearningHelper = null;
    private boolean isRecording = false;
    private TrainingSampleEntry trainingSampleEntry;
    TrainDialogFragment trainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String sensorId = intent.getStringExtra("id");

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.training_collapsing_toolbar);
        collapsingToolbar.setTitle("Train");

        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.training_floatingActionButton);
        fab.setOnClickListener(this);

        machineLearningHelper = new MachineLearningHelper(getApplicationContext());
        machineLearningHelper.setCallbacks(this);

        if(sensorId.equals("")){
            finish();
        }

        machineLearningHelper.getSensor(sensorId);
        setResult(RESULT_OK);
    }

    private void loadLabels(SensorEntry sensorEntry){
        for(String label: sensorEntry.labels){
            inflateStatusItem(label);
        }

        if(sensorEntry.labels == null || sensorEntry.labels.size() == 0 ){
            showRegisterStatusMessage();
        }
    }

    private void showRegisterStatusMessage(){
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.training_coordinator_layout);

        Snackbar.make(coordinatorLayout, "Start adding status by clicking the plus button", Snackbar.LENGTH_LONG).show();
    }

    private void inflateStatusItem(String label) {
        LinearLayout containerView = (LinearLayout) findViewById(R.id.training_labels_container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.list_item_training_status, containerView, false);

        final TextView textView = (TextView)itemView.findViewById(R.id.list_item_training_status_name);
        textView.setText(label);

        ImageButton button = (ImageButton)itemView.findViewById(R.id.list_item_training_record_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                trainingSampleEntry = new TrainingSampleEntry();
                trainFragment = new TrainDialogFragment();

                trainFragment.label = textView.getText().toString();
                trainFragment.show(getFragmentManager(), "test");
                startTraining(trainFragment.label);
            }

            ;
        });

        containerView.addView(itemView, containerView.getChildCount());
    }

    public void startTraining(String label){
        this.isRecording = true;
        trainingSampleEntry = new TrainingSampleEntry();
        machineLearningHelper.getTime();
        trainingSampleEntry.label = label;
        trainingSampleEntry.sensorId = sensorEntry._id;
        trainingSampleEntry.userId = "default";
    }

    public void stopTraining(){
        this.isRecording = false;
        machineLearningHelper.getTime();
    }

    public void cancelTraining(){
        this.isRecording = false;
    }

    public void onClick(View view){
        DialogFragment newFragment = new NewLabelDialogFragment();
        newFragment.show(getFragmentManager(), "test");
    }

    public void addStatus(String status){
        sensorEntry.labels.add(status);
        machineLearningHelper.updateSensor(sensorEntry);
        inflateStatusItem(status);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Stuff to do, dependent on requestCode and resultCode
        if(requestCode == 1)  // 1 is an arbitrary number, can be any int
        {
            // This is the return result of your DialogFragment
            if(resultCode == 1) // 1 is an arbitrary number, can be any int
            {
                // Now do what you need to do after the dialog dismisses.
            }
        }
    }

    public void onFinishTrainDialog(boolean isCompleted){
        isRecording = false;
        machineLearningHelper.getTime();
    }

    public static class TrainDialogFragment extends DialogFragment {
        public interface TrainDialogListener {
            void onFinishTrainDialog(boolean isCompleted);
        }

        public String label = "";
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Learning \"" + label + "\"...")
                    .setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TrainingActivity activity = (TrainingActivity) getActivity();
                            activity.onFinishTrainDialog(true);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

    }

    public static class NewLabelDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View content = inflater.inflate(R.layout.fragment_new_status, null);

            builder.setView(content);

            builder.setMessage("Add Status")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TrainingActivity activity = (TrainingActivity)getActivity();
                            EditText editText = (EditText)content.findViewById(R.id.fragment_new_status_name);
                            activity.addStatus(editText.getText().toString());
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_train, menu);
        return true;
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_train) {
            machineLearningHelper.train(sensorEntry._id);
        } else if (id == R.id.action_test) {
            machineLearningHelper.predict(sensorEntry._id);
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.training_coordinator_layout);
            Snackbar.make(coordinatorLayout, "Collecting sensor data", Snackbar.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSuccess(JSONObject response){

        try{
            if(response.getString("url").contains("/predict")){
                String prediction = response.getString("ret");
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.training_coordinator_layout);
                Snackbar.make(coordinatorLayout, "Prediction: " + prediction, Snackbar.LENGTH_LONG).show();
            } else if(response.getString("url").contains("/train")) {
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.training_coordinator_layout);
                Snackbar.make(coordinatorLayout, "Training Completed", Snackbar.LENGTH_LONG).show();
            } else if(response.getString("url").contains("/sensor")) {
                JSONObject obj = response.getJSONObject("ret");
                sensorEntry = new SensorEntry(obj);
                loadLabels(sensorEntry);
            } else if(response.getString("url").contains("/time")) {
                if (isRecording) {
                    trainingSampleEntry.startTime = response.getDouble("ret");
                } else {
                    trainingSampleEntry.endTime = response.getDouble("ret");
                    machineLearningHelper.insertSampling(trainingSampleEntry);
                }
            }

        }catch (JSONException e) {e.printStackTrace();}
    }

    public void onError(String response){

    }
}
