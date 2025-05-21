package com.example.ai_road;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class history_reports extends AppCompatActivity {

    ListView listView;
    EditText searchSessionName;
    Spinner spinnerImpact;

    ArrayList<AnomalyReport> reportList;          // Full list
    ArrayList<AnomalyReport> filteredReportList;  // Filtered list shown in adapter

    AnomalyAdapter adapter;

    String username = "saad";

    String[] impactOptions = {"All", "Low", "Moderate", "Hard"};  // Include "All" to reset filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_reports);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchSessionName = findViewById(R.id.searchSessionName);
        spinnerImpact = findViewById(R.id.spinnerImpact);
        listView = findViewById(R.id.listView);

        reportList = new ArrayList<>();
        filteredReportList = new ArrayList<>();

        adapter = new AnomalyAdapter(this, filteredReportList);
        listView.setAdapter(adapter);

        fetchAnomalyReports(username);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, impactOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImpact.setAdapter(spinnerAdapter);

        searchSessionName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterReports();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        spinnerImpact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filterReports();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchAnomalyReports(String username) {
        String url = "http://10.0.2.2/airoad_backend/api/getAnomalyReports.php?username=" + username;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray dataArray = response.getJSONArray("data");

                            reportList.clear();
                            filteredReportList.clear();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                String session = item.getString("session_name");
                                String timestamp = item.getString("timestamp");
                                String impact = item.getString("impact");

                                AnomalyReport report = new AnomalyReport(session, timestamp, impact);
                                reportList.add(report);
                            }
                            filteredReportList.addAll(reportList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        queue.add(jsonObjectRequest);
    }

    private void filterReports() {
        String sessionFilter = searchSessionName.getText().toString().toLowerCase().trim();
        String impactFilter = spinnerImpact.getSelectedItem().toString();

        filteredReportList.clear();

        for (AnomalyReport report : reportList) {
            boolean matchesSession = report.sessionName.toLowerCase().contains(sessionFilter);
            boolean matchesImpact = impactFilter.equals("All") || report.impact.equalsIgnoreCase(impactFilter);

            if (matchesSession && matchesImpact) {
                filteredReportList.add(report);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
