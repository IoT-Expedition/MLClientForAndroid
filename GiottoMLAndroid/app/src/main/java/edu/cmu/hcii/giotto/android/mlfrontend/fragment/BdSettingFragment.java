package edu.cmu.hcii.giotto.android.mlfrontend.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.cmu.hcii.giotto.android.mlfrontend.R;

/**
 * Created by ehayashi on 4/2/16.
 */
public class BdSettingFragment extends Fragment {

    private View containerView;

    public BdSettingFragment() {
    }

    public static BdSettingFragment newInstance() {
        BdSettingFragment fragment = new BdSettingFragment();
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        containerView = inflater.inflate(R.layout.fragment_bd_setting, container, false);
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

        final EditText bdServerEditText = (EditText) containerView.findViewById(R.id.building_depot_server_editText);
        final EditText bdPortEditText = (EditText) containerView.findViewById(R.id.building_depot_port_editText);
        final EditText bdApiPrefixTextEdit = (EditText) containerView.findViewById(R.id.building_depot_api_prefix_exitText);
        final EditText oAuthIdTextEdit = (EditText) containerView.findViewById(R.id.oauth_id_editText);
        final EditText oAuthKeyTextEdit = (EditText) containerView.findViewById(R.id.oauth_secret_editText);

        bdServerEditText.setText(prefs.getString("bd_server","https://bd.example.com"));
        bdPortEditText.setText(prefs.getString("bd_port","82"));
        bdApiPrefixTextEdit.setText(prefs.getString("bd_api_prefix","/api"));
        oAuthIdTextEdit.setText(prefs.getString("oauth_id","LLwSpemrRGwM5hyEuyxbrF0ri6oquwfvYAkLo0XY"));
        oAuthKeyTextEdit.setText(prefs.getString("oauth_key","0NWDuJGyG0ZVZohycCp83uCO26aacGIHT7AhhGDOgvqHBvft0N"));
    }

    private void saveSettings(View containerView){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();

        final EditText bdServerEditText = (EditText) containerView.findViewById(R.id.building_depot_server_editText);
        final EditText bdPortEditText = (EditText) containerView.findViewById(R.id.building_depot_port_editText);
        final EditText bdApiPrefixTextEdit = (EditText) containerView.findViewById(R.id.building_depot_api_prefix_exitText);
        final EditText oAuthIdTextEdit = (EditText) containerView.findViewById(R.id.oauth_id_editText);
        final EditText oAuthKeyTextEdit = (EditText) containerView.findViewById(R.id.oauth_secret_editText);

        editor.putString("bd_server", bdServerEditText.getText().toString());
        editor.putString("bd_port", bdPortEditText.getText().toString());
        editor.putString("bd_api_prefix", bdApiPrefixTextEdit.getText().toString());
        editor.putString("oauth_id", oAuthIdTextEdit.getText().toString());
        editor.putString("oauth_key", oAuthKeyTextEdit.getText().toString());

        editor.apply();
    }
}
