package com.example.myapplication2;

import com.example.myapplication2.R;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.graphics.Color;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText firstNameInput, lastNameInput, middleInitialInput, emailInput, passwordInput, confirmPasswordInput, phoneInput, birthDateInput;
    Button registerButton;
    TextView loginText;
    CheckBox termsCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI
        firstNameInput = findViewById(R.id.first_name_input);
        lastNameInput = findViewById(R.id.last_name_input);
        middleInitialInput = findViewById(R.id.middle_initial_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        phoneInput = findViewById(R.id.phone_input);
        birthDateInput = findViewById(R.id.birth_date_input);
        registerButton = findViewById(R.id.register_button);
        loginText = findViewById(R.id.login_text);
        termsCheckBox = findViewById(R.id.terms_checkbox);

        // Initialize Eye Icon Toggles
        ImageView togglePassword = findViewById(R.id.toggle_password_visibility);
        ImageView toggleConfirmPassword = findViewById(R.id.toggle_confirm_password_visibility);

        final boolean[] isPasswordVisible = {false};
        final boolean[] isConfirmPasswordVisible = {false};

        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_closed);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_open);
            }
            passwordInput.setSelection(passwordInput.getText().length());
            isPasswordVisible[0] = !isPasswordVisible[0];
        });

        toggleConfirmPassword.setOnClickListener(v -> {
            if (isConfirmPasswordVisible[0]) {
                confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleConfirmPassword.setImageResource(R.drawable.ic_eye_closed);
            } else {
                confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleConfirmPassword.setImageResource(R.drawable.ic_eye_open);
            }
            confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
            isConfirmPasswordVisible[0] = !isConfirmPasswordVisible[0];
        });

        // Add hints
        firstNameInput.setHint(Html.fromHtml(getString(R.string.hint_first_name)));
        lastNameInput.setHint(Html.fromHtml(getString(R.string.hint_last_name)));
        phoneInput.setHint(Html.fromHtml(getString(R.string.hint_phone)));
        birthDateInput.setHint(Html.fromHtml(getString(R.string.hint_birth)));
        emailInput.setHint(Html.fromHtml(getString(R.string.hint_email)));
        passwordInput.setHint(Html.fromHtml(getString(R.string.hint_password)));
        confirmPasswordInput.setHint(Html.fromHtml(getString(R.string.hint_confirm_password)));

        // Terms & Conditions
        String text = "I agree to the Terms and Conditions *";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull android.view.View widget) {
                showTermsDialog();
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };
        int start = text.indexOf("Terms and Conditions");
        int end = start + "Terms and Conditions".length();
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsCheckBox.setText(ss);
        termsCheckBox.setMovementMethod(LinkMovementMethod.getInstance());

        // Phone input length limit
        phoneInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });

        // Birthdate picker
        birthDateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                    (DatePicker view, int year1, int month1, int dayOfMonth) -> {
                        String date = String.format("%04d-%02d-%02d", year1, (month1 + 1), dayOfMonth);
                        birthDateInput.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Register button click
        registerButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String middleInitial = middleInitialInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String birthDate = birthDateInput.getText().toString().trim();

            if (!termsCheckBox.isChecked()) {
                Toast.makeText(this, "You must agree to the Terms & Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty() ||
                    phone.isEmpty() || birthDate.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 12 characters, include letters, numbers, and a special character", Toast.LENGTH_LONG).show();
            } else if (!isValidPhone(phone)) {
                Toast.makeText(this, "Enter a valid phone number (11 digits)", Toast.LENGTH_LONG).show();
            } else {
                registerUser(firstName, lastName, middleInitial, email, password, phone, birthDate);
            }
        });

        // Login text
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$";
        return password.matches(regex);
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^\\d{11}$");
    }

    private void registerUser(String firstName, String lastName, String middleInitial, String email,
                              String password, String phone, String birthDate) {

        ApiService apiService = RetrofitClient.getApiService();
        Call<RegisterResponse> call = apiService.register(
                firstName, lastName, middleInitial, email, password, phone, birthDate
        );

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.isSuccess()) {
                        Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTermsDialog() {
        TextView termsTextView = new TextView(this);
        termsTextView.setText(
                "Terms and Conditions\n\n" +
                        "1. Orders are final and cannot be canceled once confirmed.\n" +
                        "2. Users are responsible for providing accurate information.\n" +
                        "3. All products or services are provided as-is.\n" +
                        "4. Terms may be updated at any time.\n\n" +
                        "By using this service, you agree to all terms."
        );
        termsTextView.setPadding(48,48,48,48);
        termsTextView.setScrollBarStyle(TextView.SCROLLBARS_INSIDE_INSET);
        termsTextView.setVerticalScrollBarEnabled(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        builder.setView(termsTextView);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
        termsTextView.post(() -> termsTextView.setMovementMethod(new android.text.method.ScrollingMovementMethod()));
    }
}
