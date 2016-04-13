package edu.cmu.hcii.giotto.android.mlfrontend.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.cmu.hcii.giotto.android.mlfrontend.R;
import edu.cmu.hcii.giotto.android.mlfrontend.activity.EditSensorActivity;
import edu.cmu.hcii.giotto.android.mlfrontend.activity.SensorActivity;
import edu.cmu.hcii.giotto.android.mlfrontend.application.SensorAdapter;
import edu.cmu.hcii.giotto.android.mlfrontend.application.SensorEntry;
import edu.cmu.hcii.giotto.android.mlfrontend.helper.MachineLearningHelper;


public class SensorListFragment extends Fragment implements AdapterView.OnItemClickListener, MachineLearningHelper.Callbacks {

    private View containerView;
    private MachineLearningHelper machineLearningHelper;

    public SensorListFragment() {
    }

    public static SensorListFragment newInstance() {
        SensorListFragment fragment = new SensorListFragment();
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        containerView = inflater.inflate(R.layout.fragment_sensor_list, container, false);

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) containerView.findViewById(R.id.first_coordinator_layout);
        FloatingActionButton fab = (FloatingActionButton) containerView.findViewById(R.id.first_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add a sensor
                Intent intent = new Intent(getActivity(), EditSensorActivity.class);
                intent.putExtra("id", "");
                startActivityForResult(intent, 0);
            }
        });

        ListView sensorListView = (ListView)containerView.findViewById(R.id.sensor_list_view);

        //configureSensorListItems(sensorListView);

        machineLearningHelper = new MachineLearningHelper(getContext());
        machineLearningHelper.setCallbacks(this);
        machineLearningHelper.getSensors();

        return containerView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), SensorActivity.class);

        TextView textView = (TextView) view.findViewById(R.id.entry_id);
        intent.putExtra("id", textView.getText().toString());
        startActivityForResult(intent, 1);
    }

    public void refresh(){
        machineLearningHelper.getSensors();
    }

    private void configureSensorListItems(ArrayList<SensorEntry> sensors){
        ListView listView = (ListView)containerView.findViewById(R.id.sensor_list_view);

        ArrayList<String> strings = new ArrayList<String>();
        for(SensorEntry sensor: sensors){
            strings.add(sensor.name);
        }

        // Configure sensor list
        SensorAdapter adapter = new SensorAdapter(getActivity(), R.layout.list_item_sensor, sensors);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(this);
    }

    public void onSuccess(JSONObject response) {
        try {
            JSONArray array = response.getJSONArray("ret");
            Log.v("ML",array.toString());

            ArrayList<SensorEntry> sensors = new ArrayList<SensorEntry>();
            SensorEntry sensor;

            for (int i=0; i<array.length(); i++) {
                JSONObject obj = (JSONObject)array.get(i);
                sensor = new SensorEntry(obj);
                sensors.add(sensor);

                ListView sensorListView = (ListView)containerView.findViewById(R.id.sensor_list_view);
                configureSensorListItems(sensors);
            }

        }
        catch(Exception e){

        }
    }

    public void onError(String response){

    }

}
