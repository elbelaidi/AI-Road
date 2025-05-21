package com.example.ai_road;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerBtn;
    private TextView backToLoginBtn;

    private static final String REGISTER_URL = "http://10.0.2.2/airoad_backend/api/registerApi.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerBtn = findViewById(R.id.registerBtn);
        backToLoginBtn = findViewById(R.id.backToLoginBtn);

        registerBtn.setOnClickListener(v -> registerUser());

        backToLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill in all fields", false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match", false);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, REGISTER_URL, jsonBody,
                    response -> {
                        try {
                            boolean success = response.optBoolean("success", false);
                            String message = response.optString("message", "No message");

                            if (success) {
                                showToast("Registered successfully!", true);
                                startActivity(new Intent(Register.this, Login.class));
                                finish();
                            } else {
                                showToast("Registration failed: " + message, false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("Response parse error", false);
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            String body = new String(error.networkResponse.data);
                            Log.e("VolleyError", "Status code: " + error.networkResponse.statusCode + ", body: " + body);
                        } else {
                            Log.e("VolleyError", "No network response", error);
                        }
                        showToast("Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"), false);
                    }
            );

            queue.add(jsonRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            showToast("JSON error: " + e.getMessage(), false);
        }
    }

    private void showToast(String message, boolean success) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                success ? R.layout.toast_success : R.layout.toast_failed,
                null
        );
        TextView text = layout.findViewById(R.id.toastText);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}