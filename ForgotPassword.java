package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.ApiService;
import com.example.myapplication2.RetrofitClient;
import com.example.myapplication2.models.UserUpdateResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    EditText emailInput;
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.email_input);
        resetButton = findViewById(R.id.reset_button);

        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            sendOtpRequest(email);
        });
    }

    private void sendOtpRequest(String email) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<UserUpdateResponse> call = apiService.forgotPassword(email);

        call.enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserUpdateResponse res = response.body();
                    Toast.makeText(ForgotPassword.this, res.getMessage(), Toast.LENGTH_LONG).show();

                    if (res.isSuccess()) {
                        // OTP sent successfully, go to OTP verification screen
                        Intent intent = new Intent(ForgotPassword.this, ForgotOtpVerificationActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(ForgotPassword.this, "Failed to send OTP", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                Toast.makeText(ForgotPassword.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
