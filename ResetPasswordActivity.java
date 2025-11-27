package com.example.myapplication2;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.myapplication2.ApiService;


import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication2.models.UserUpdateResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText newPasswordInput, confirmPasswordInput;
    ImageView eyeNew, eyeConfirm;
    Button confirmButton;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordInput = findViewById(R.id.new_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        eyeNew = findViewById(R.id.eye_new_password);
        eyeConfirm = findViewById(R.id.eye_confirm_password);
        confirmButton = findViewById(R.id.confirm_button);
        email = getIntent().getStringExtra("email");

        eyeNew.setOnClickListener(v -> togglePasswordVisibility(newPasswordInput, eyeNew));
        eyeConfirm.setOnClickListener(v -> togglePasswordVisibility(confirmPasswordInput, eyeConfirm));

        confirmButton.setOnClickListener(v -> {
            String newPass = newPasswordInput.getText().toString().trim();
            String confirmPass = confirmPasswordInput.getText().toString().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = RetrofitClient.getApiService();
            Call<UserUpdateResponse> call = apiService.resetPassword(email, newPass); // Create endpoint in backend
            call.enqueue(new Callback<UserUpdateResponse>() {
                @Override
                public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ResetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        finish(); // Back to login
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Failed to reset password", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                    Toast.makeText(ResetPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void togglePasswordVisibility(EditText input, ImageView eye) {
        if (input.getTransformationMethod() instanceof PasswordTransformationMethod) {
            input.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            eye.setImageResource(R.drawable.ic_eye_open);
        } else {
            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            eye.setImageResource(R.drawable.ic_eye_closed);
        }
        input.setSelection(input.getText().length());
    }
}
