package com.example.ai_road;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private TextView welcomeText;
    private Button btnStartStopDetection, btnHistory, btnLogout;
    private GoogleMap googleMap;
    private boolean detectionStarted = false;

    private FusedLocationProviderClient fusedLocationClient;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private OkHttpClient httpClient;

    private Handler handler = new Handler();
    private Runnable sendRunnable;

    private float latestX = 0f, latestY = 0f, latestZ = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeText);
        btnStartStopDetection = findViewById(R.id.btnStartStopDetection);
        btnHistory = findViewById(R.id.btnHistory);
        btnLogout = findViewById(R.id.btnLogout);

        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            welcomeText.setText("Welcome, " + username + " !");
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        httpClient = new OkHttpClient();

        MapFragment mapFragment = MapFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.mapPlaceholder, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        sendRunnable = new Runnable() {
            @Override
            public void run() {
                if (detectionStarted) {
                    sendReadingToApi(latestX, latestY, latestZ);
                    handler.postDelayed(this, 5000);
                }
            }
        };

        btnStartStopDetection.setOnClickListener(v -> {
            detectionStarted = !detectionStarted;
            btnStartStopDetection.setText(detectionStarted ? "Stop Detection" : "Start Detection");

            if (detectionStarted) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                handler.post(sendRunnable);
                btnStartStopDetection.setBackgroundColor(getResources().getColor(R.color.detection_start));
            } else {
                sensorManager.unregisterListener(this);
                handler.removeCallbacks(sendRunnable);
                btnStartStopDetection.setBackgroundColor(getResources().getColor(R.color.detection_stop));
            }
        });

        btnHistory.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "History & Reports clicked", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (detectionStarted) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            handler.post(sendRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(sendRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!detectionStarted) return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            latestX = event.values[0];
            latestY = event.values[1];
            latestZ = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void sendReadingToApi(float x, float y, float z) {
        if (fusedLocationClient == null) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                String username = getIntent().getStringExtra("username");
                JSONObject json = new JSONObject();
                try {
                    json.put("username", username);
                    json.put("gps_lat", location.getLatitude());
                    json.put("gps_lng", location.getLongitude());
                    json.put("accel_x", x);
                    json.put("accel_y", y);
                    json.put("accel_z", z);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                RequestBody body = RequestBody.create(json.toString(), JSON);
                Request request = new Request.Builder()
                        .url("http://192.168.100.6/airoad_backend/api/add_readingApi.php")
                        .post(body)
                        .build();

                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                "Failed to send reading", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        response.close();
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                            googleMap.addMarker(new MarkerOptions().position(userLatLng).title("You are here"));
                        } else {
                            Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (googleMap != null) {
                    onMapReady(googleMap);
                }
            } else {
                Toast.makeText(this, "Location permission required to show your position", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
