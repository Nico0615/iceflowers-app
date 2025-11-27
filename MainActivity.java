package com.example.myapplication2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText emailInput, passwordInput;
    ImageView togglePassword;
    Button loginButton;
    TextView registerText, forgotPasswordText;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        togglePassword = findViewById(R.id.toggle_password_visibility);
        loginButton = findViewById(R.id.login_button);
        registerText = findViewById(R.id.register_text);
        forgotPasswordText = findViewById(R.id.forgot_password_text);

        // Toggle password visibility
        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_closed);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_open);
            }
            passwordInput.setSelection(passwordInput.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        loginButton.setOnClickListener(v -> loginUser());

        registerText.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class))
        );

        forgotPasswordText.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ForgotPassword.class))
        );
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            safeToast("Email and password are required");
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<LoginResponse> call = apiService.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body() == null) {
                    safeToast("Login failed: empty response");
                    Log.e(TAG, "Empty LoginResponse: " + response.toString());
                    return;
                }

                LoginResponse loginResponse = response.body();

                if (loginResponse.isSuccess()) {
                    safeToast("Welcome " + loginResponse.getFirstName());

                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("first_name", loginResponse.getFirstName());
                    editor.putString("email", email);
                    editor.putInt("loggedInUserId", loginResponse.getUserId());
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    safeToast(loginResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                safeToast("Login error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
                Log.e(TAG, "Login failure", t);
            }
        });
    }

    private void safeToast(String message) {
        if (message == null || message.trim().isEmpty()) {
            message = "Something went wrong";
        }
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
