package com.example.ai_road;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Handler;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(v -> {
            String user = usernameInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                showToast("Please enter username and password", false);
                return;
            }

            String url = "http://10.0.2.2/airoad_backend/api/loginApi.php";

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", user);
                jsonBody.put("password", pass);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if (success) {


                                new Handler().postDelayed(() -> {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.putExtra("username", user);
                                    startActivity(intent);
                                    finish();
                                }, 1000);
                                showToast(message, true);
                            }
                            else {
                                showToast(message, false);
                                usernameInput.setText("");
                                passwordInput.setText("");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("Response parsing error", false);
                        }
                    },
                    error -> {
                        showToast("Network error: " + error.getMessage(), false);
                    }
            );

            Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
        });

        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
        });
    }

    private void showToast(String message, boolean success) {
        LayoutInflater inflater = getLayoutInflater();
        int layoutId = success ? R.layout.toast_success : R.layout.toast_failed;
        View layout = inflater.inflate(layoutId, findViewById(R.id.toast_root));

        TextView toastText = layout.findViewById(R.id.toastText);
        toastText.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
