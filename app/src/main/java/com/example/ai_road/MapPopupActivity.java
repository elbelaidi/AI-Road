package com.example.ai_road;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class MapPopupActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private double lat = 0, lng = 0;

    private String sessionName, timestamp, impact;

    private TextView impactTextView, sessionNameTextView, timestampTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_popup);

        sessionName = getIntent().getStringExtra("sessionName");
        timestamp = getIntent().getStringExtra("timestamp");
        impact = getIntent().getStringExtra("impact");

        impactTextView = findViewById(R.id.impactTextView);
        sessionNameTextView = findViewById(R.id.sessionNameTextView);
        timestampTextView = findViewById(R.id.timestampTextView);

        sessionNameTextView.setText("Session: " + sessionName);
        timestampTextView.setText("Time: " + timestamp);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button buttonBackHome = findViewById(R.id.backButton);
        buttonBackHome.setOnClickListener(v -> finish());

        fetchAnomalyLocationFromAPI(sessionName, timestamp);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (lat != 0 && lng != 0) {
            updateMapLocation(new LatLng(lat, lng));
        }
    }

    private void updateMapLocation(LatLng location) {
        if (googleMap != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(location).title("Anomaly Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
        }
    }

    private void fetchAnomalyLocationFromAPI(String sessionName, String timestamp) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://10.0.2.2/airoad_backend/api/getAnomalyBySession.php?sessionName=" +
                Uri.encode(sessionName) + "&timestamp=" + Uri.encode(timestamp);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            lat = json.getDouble("gps_lat");
                            lng = json.getDouble("gps_lng");
                            impact = json.getString("impact");

                            updateMapLocation(new LatLng(lat, lng));
                            impactTextView.setText("Impact: " + impact);
                        } else {
                            String error = json.optString("error", "Unknown error");
                            impactTextView.setText("Error: " + error);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        impactTextView.setText("Error parsing response");
                    }
                },
                error -> {
                    error.printStackTrace();
                    impactTextView.setText("Network error");
                });

        queue.add(stringRequest);
    }
}
