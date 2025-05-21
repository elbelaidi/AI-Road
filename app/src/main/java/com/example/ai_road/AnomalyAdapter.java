package com.example.ai_road;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AnomalyAdapter extends ArrayAdapter<AnomalyReport> {

    public AnomalyAdapter(Context context, List<AnomalyReport> reports) {
        super(context, 0, reports);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AnomalyReport report = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_anomaly, parent, false);
        }

        TextView sessionName = convertView.findViewById(R.id.sessionNameTextView);
        TextView timestamp = convertView.findViewById(R.id.sessionDateTextView);
        TextView impact = convertView.findViewById(R.id.impactTextView);

        sessionName.setText("Session: " + report.sessionName);
        timestamp.setText("Time: " + report.timestamp);
        impact.setText("Impact: " + report.impact);

        return convertView;
    }
}
