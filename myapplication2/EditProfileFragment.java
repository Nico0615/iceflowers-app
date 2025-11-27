package com.example.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication2.api.ApiClient;
import com.example.myapplication2.models.User;
import com.example.myapplication2.models.UserUpdateResponse;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private EditText etFirstName, etMiddleInitial, etLastName, etEmail, etPhone, etBirthdate;
    private ImageView profileAvatar;
    private Button btnUpdateProfile;

    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etFirstName = view.findViewById(R.id.et_first_name);
        etMiddleInitial = view.findViewById(R.id.et_middle_initial);
        etLastName = view.findViewById(R.id.et_last_name);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        etBirthdate = view.findViewById(R.id.et_birthdate);
        profileAvatar = view.findViewById(R.id.profile_avatar);

        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        btnUpdateProfile.setOnClickListener(v -> updateUserProfile());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshProfile();
    }

    private void refreshProfile() {
        loadCurrentUserId();
        loadUserProfile();
    }

    private void loadCurrentUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("loggedInUserId", -1);
    }

    private void loadUserProfile() {
        if (userId == -1) {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUser(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    etFirstName.setText(user.getFirst_name());
                    etMiddleInitial.setText(user.getMiddle_initial());
                    etLastName.setText(user.getLast_name());
                    etEmail.setText(user.getEmail());
                    etPhone.setText(user.getPhone_number());
                    etBirthdate.setText(convertDateToProfileFormat(user.getBirth_date()));
                } else {
                    Toast.makeText(getContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String middleInitial = etMiddleInitial.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthdate = etBirthdate.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "First Name, Last Name, and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        String birthdateForServer = convertDateToServerFormat(birthdate);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserUpdateResponse> call = apiService.updateUser(
                userId, firstName, middleInitial, lastName, email, phone, birthdateForServer
        );

        call.enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertDateToProfileFormat(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yy/MM/dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yy");
            return outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }

    private String convertDateToServerFormat(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yy/MM/dd");
            return outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }
}
