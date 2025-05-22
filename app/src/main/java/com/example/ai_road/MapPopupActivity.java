package com.example.ai_road;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapPopupActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_popup);

        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        Button buttonBackHome = findViewById(R.id.backButton);
        buttonBackHome.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng position = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(position).title("Anomaly Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
    }
}
