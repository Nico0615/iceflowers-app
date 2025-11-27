package com.example.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication2.api.ApiClient;
import com.example.myapplication2.ApiService;
import com.example.myapplication2.models.User;
import com.example.myapplication2.models.UserUpdateResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private ImageView profileAvatar;
    private LinearLayout btnEditProfile, btnOrderHistory, btnChangePassword, btnLogout;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        profileAvatar = view.findViewById(R.id.profile_avatar);

        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnOrderHistory = view.findViewById(R.id.btn_order_history);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);

        // -----------------------
        // Buttons
        // -----------------------
        btnEditProfile.setOnClickListener(v -> openEditProfile());

        btnOrderHistory.setOnClickListener(v -> {
            OrderHistoryFragment fragment = new OrderHistoryFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentUserId();
        loadUserProfile();
    }

    private void loadCurrentUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("loggedInUserId", -1);
    }

    private void loadUserProfile() {
        if (userId == -1) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUser(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvUserName.setText(user.getFirst_name() + " " + user.getLast_name());
                    tvUserEmail.setText(user.getEmail());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) { }
        });
    }

    private void openEditProfile() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new EditProfileFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logout() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_change_password, null);

        EditText etCurrent = dialogView.findViewById(R.id.et_current_password);
        EditText etNew = dialogView.findViewById(R.id.et_new_password);
        EditText etConfirm = dialogView.findViewById(R.id.et_confirm_password);
        Button btnChange = dialogView.findViewById(R.id.btn_change_password);

        ImageView toggleCurrent = dialogView.findViewById(R.id.toggle_current_password);
        ImageView toggleNew = dialogView.findViewById(R.id.toggle_new_password);
        ImageView toggleConfirm = dialogView.findViewById(R.id.toggle_confirm_password);

        final boolean[] isCurrentVisible = {false};
        final boolean[] isNewVisible = {false};
        final boolean[] isConfirmVisible = {false};

        toggleCurrent.setOnClickListener(v -> {
            if (isCurrentVisible[0]) {
                etCurrent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleCurrent.setImageResource(R.drawable.ic_eye_closed);
            } else {
                etCurrent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleCurrent.setImageResource(R.drawable.ic_eye_open);
            }
            etCurrent.setSelection(etCurrent.getText().length());
            isCurrentVisible[0] = !isCurrentVisible[0];
        });

        toggleNew.setOnClickListener(v -> {
            if (isNewVisible[0]) {
                etNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleNew.setImageResource(R.drawable.ic_eye_closed);
            } else {
                etNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleNew.setImageResource(R.drawable.ic_eye_open);
            }
            etNew.setSelection(etNew.getText().length());
            isNewVisible[0] = !isNewVisible[0];
        });

        toggleConfirm.setOnClickListener(v -> {
            if (isConfirmVisible[0]) {
                etConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleConfirm.setImageResource(R.drawable.ic_eye_closed);
            } else {
                etConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleConfirm.setImageResource(R.drawable.ic_eye_open);
            }
            etConfirm.setSelection(etConfirm.getText().length());
            isConfirmVisible[0] = !isConfirmVisible[0];
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnChange.setOnClickListener(v -> {
            String current = etCurrent.getText().toString().trim();
            String newPass = etNew.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (TextUtils.isEmpty(current) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirm)) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirm)) {
                Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<UserUpdateResponse> call = apiService.changePassword(userId, current, newPass);

            call.enqueue(new Callback<UserUpdateResponse>() {
                @Override
                public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserUpdateResponse result = response.body();
                        Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        if (result.isSuccess()) dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
