package edu.cmu.hcii.giotto.android.mlfrontend.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.cmu.hcii.giotto.android.mlfrontend.R;

/**
 * Created by ehayashi on 9/24/15.
 */
public class SensorAdapter extends ArrayAdapter<SensorEntry> {
    private LayoutInflater layoutInflater_;

    public SensorAdapter(Context context, int textViewResourceId, List<SensorEntry> objects) {
        super(context, textViewResourceId, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        SensorEntry item = (SensorEntry)getItem(position);

        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.list_item_sensor, null);
        }

        TextView textView;
        textView = (TextView)convertView.findViewById(R.id.name_text);
        textView.setText(item.name);

        textView = (TextView)convertView.findViewById(R.id.description_text);
        textView.setText(item.description);

        textView = (TextView)convertView.findViewById(R.id.entry_id);
        textView.setText(item._id);

        return convertView;
    }
}
