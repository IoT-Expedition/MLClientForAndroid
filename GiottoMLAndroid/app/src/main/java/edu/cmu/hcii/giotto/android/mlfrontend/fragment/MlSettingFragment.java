package edu.cmu.hcii.giotto.android.mlfrontend.fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.cmu.hcii.giotto.android.mlfrontend.R;


/**
 * Created by ehayashi on 3/28/16.
 */
public class MlSettingFragment extends Fragment {

    private View containerView;

    public MlSettingFragment() {
    }

    public static MlSettingFragment newInstance() {
        MlSettingFragment fragment = new MlSettingFragment();
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        containerView = inflater.inflate(R.layout.fragment_ml_setting, container, false);
        loadSettings(containerView);

        return containerView;
    }

    @Override
    public void onPause(){
        super.onPause();
        saveSettings(containerView);
    }

    private void loadSettings(View containerView){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        final EditText mlServerEditText = (EditText) containerView.findViewById(R.id.ml_server_editText);
        final EditText mlPortEditText = (EditText) containerView.findViewById(R.id.ml_port_editText);

        mlServerEditText.setText(prefs.getString("ml_server","https://ml.example.com"));
        mlPortEditText.setText(prefs.getString("ml_port","5000"));
    }

    private void saveSettings(View containerView){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();

        final EditText mlServerEditText = (EditText) containerView.findViewById(R.id.ml_server_editText);
        final EditText mlPortEditText = (EditText) containerView.findViewById(R.id.ml_port_editText);

        editor.putString("ml_server", mlServerEditText.getText().toString());
        editor.putString("ml_port", mlPortEditText.getText().toString());

        editor.apply();
    }

}
