package com.mobile2.uts_elsid.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mobile2.uts_elsid.LoginActivity;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.api.ApiClient;
import com.mobile2.uts_elsid.api.ApiService;
import com.mobile2.uts_elsid.api.LoginResponse;
import com.mobile2.uts_elsid.api.UserResponse;
import com.mobile2.uts_elsid.databinding.FragmentProfileBinding;
import com.mobile2.uts_elsid.model.User;
import com.mobile2.uts_elsid.utils.SessionManager;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());

        // Load user data
        loadUserProfile();

        MaterialButton wishlistButton = binding.wishlistButton;
        wishlistButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);
            navController.navigate(R.id.navigation_wishlist);
        });

        binding.aboutButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);
            navController.navigate(R.id.navigation_about);
        });

        // Setup click listeners
        binding.editProfileButton.setOnClickListener(v -> openEditProfile());
        binding.logoutButton.setOnClickListener(v -> logout());

        return binding.getRoot();
    }

    private void loadUserProfile() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);

        if (binding != null) {
            binding.loadingIndicator.setVisibility(View.VISIBLE);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserResponse> call = apiService.getUser();

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!isAdded() || binding == null) return;

                binding.loadingIndicator.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if (userResponse.getStatus() == 1 && !userResponse.getUsers().isEmpty()) {
                        User currentUser = null;
                        String currentUserEmail = sessionManager.getUserData().getUser().getEmail();

                        for (User user : userResponse.getUsers()) {
                            if (user.getEmail().equals(currentUserEmail)) {
                                currentUser = user;
                                break;
                            }
                        }

                        if (currentUser != null) {
                            updateUI(currentUser);
                        }
                    }
                }
            }


            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (!isAdded() || binding == null) return;

                binding.loadingIndicator.setVisibility(View.GONE);
                Toasty.error(requireContext(), "Failed to load profile", Toasty.LENGTH_SHORT).show();
            }

        });
    }

    private void updateUI(User user) {
        binding.fullnameText.setText(user.getFullname());
        binding.emailText.setText(user.getEmail());

        // Handle phone
        String phone = !TextUtils.isEmpty(user.getPhone()) ? user.getPhone() : "No phone number";
        binding.phoneText.setText(phone);

        // Handle address
        String address = !TextUtils.isEmpty(user.getAddress()) ?
                String.format("%s\n%s, %s %s", user.getAddress(), user.getCity(),
                        user.getProvince(), user.getPostalCode()) :
                "No address provided";
        binding.addressText.setText(address);

        // Load avatar
        if (!TextUtils.isEmpty(user.getAvatar())) {
            String avatarUrl = "https://mobile2.ndp.my.id/" + user.getAvatar();
            Glide.with(this)
                    .load(avatarUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .circleCrop()
                    .into(binding.avatarImage);
        }
    }

    private void openEditProfile() {
        Navigation.findNavController(requireView())
                .navigate(R.id.navigation_edit_profile);
    }

    private void logout() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sessionManager.logout();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finishAffinity();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}