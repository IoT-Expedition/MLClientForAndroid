package edu.cmu.hcii.giotto.android.mlfrontend.fragment;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.cmu.hcii.giotto.android.mlfrontend.R;
import edu.cmu.hcii.giotto.android.mlfrontend.application.GIoTTOApplication;

/**
 * Created by ehayashi on 4/2/16.
 */
public class LocationEmulationFragment extends Fragment {
    private View containerView;

    public LocationEmulationFragment() {
    }

    public static LocationEmulationFragment newInstance() {
        LocationEmulationFragment fragment = new LocationEmulationFragment();
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        containerView = inflater.inflate(R.layout.fragment_location_emulation, container, false);
        loadSettings(containerView);

        return containerView;
    }

    @Override
    public void onPause(){
        super.onPause();
        ((GIoTTOApplication)getActivity().getApplication()).refreshNearbySensors();
        saveSettings(containerView);
    }

    private void loadSettings(View containerView){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        final EditText locationNameEditText = (EditText) containerView.findViewById(R.id.location_name_editText);


        locationNameEditText.setText(prefs.getString("location_emulation","ML_Demo"));

    }

    private void saveSettings(View containerView){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();

        final EditText locationNameEditText = (EditText) containerView.findViewById(R.id.location_name_editText);

        editor.putString("location_emulation", locationNameEditText.getText().toString());

        editor.apply();
    }
}
