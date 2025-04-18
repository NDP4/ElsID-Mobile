package com.mobile2.uts_elsid.ui.profile;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
//import android.support.v4.content.CursorLoader;
import android.database.Cursor;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.api.ApiClient;
import com.mobile2.uts_elsid.api.ApiService;
import com.mobile2.uts_elsid.api.UserResponse;
import com.mobile2.uts_elsid.databinding.FragmentEditProfileBinding;
import com.mobile2.uts_elsid.model.User;
import com.mobile2.uts_elsid.utils.SessionManager;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.JSONObject;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;
    private SessionManager sessionManager;
    private User currentUser;
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());

        loadUserData();
        setupListeners();

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .circleCrop()
                        .into(binding.avatarImage);

                uploadAvatar(uri);
            }
        });

        return binding.getRoot();
    }

    private void loadUserData() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserResponse> call = apiService.getUser();

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                binding.loadingIndicator.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if (userResponse.getStatus() == 1 && !userResponse.getUsers().isEmpty()) {
                        String currentUserEmail = sessionManager.getUserData().getUser().getEmail();

                        for (User user : userResponse.getUsers()) {
                            if (user.getEmail().equals(currentUserEmail)) {
                                currentUser = user;
                                populateUserData(user);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                binding.loadingIndicator.setVisibility(View.GONE);
                Toasty.error(requireContext(), "Failed to load user data", Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUserData(User user) {
        binding.fullnameInput.setText(user.getFullname());
        binding.phoneInput.setText(user.getPhone());
        binding.addressInput.setText(user.getAddress());
        binding.cityInput.setText(user.getCity());
        binding.provinceInput.setText(user.getProvince());
        binding.postalCodeInput.setText(user.getPostalCode());

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            String avatarUrl = "https://mobile2.ndp.my.id/" + user.getAvatar();
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .circleCrop()
                    .into(binding.avatarImage);
        }
    }

    private void setupListeners() {
        binding.saveButton.setOnClickListener(v -> saveChanges());
        binding.changeAvatarButton.setOnClickListener(v -> openImagePicker());
    }

    private void saveChanges() {
        if (currentUser == null) {
            Toasty.error(requireContext(), "User data not found", Toasty.LENGTH_SHORT).show();
            return;
        }

        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.saveButton.setEnabled(false);

        String fullname = binding.fullnameInput.getText().toString().trim();
        String phone = binding.phoneInput.getText().toString().trim();
        String address = binding.addressInput.getText().toString().trim();
        String city = binding.cityInput.getText().toString().trim();
        String province = binding.provinceInput.getText().toString().trim();
        String postalCode = binding.postalCodeInput.getText().toString().trim();

        if (fullname.isEmpty()) {
            binding.fullnameInput.setError("Full name is required");
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.saveButton.setEnabled(true);
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.updateProfile(
                "update",
                currentUser.getId(),
                fullname,
                phone,
                address,
                city,
                province,
                postalCode
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.saveButton.setEnabled(true);

                if (response.code() == 200) {
                    Toasty.success(requireContext(), "Profile updated successfully", Toasty.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    Toasty.error(requireContext(), "Update failed: " + response.message(), Toasty.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.saveButton.setEnabled(true);
                Toasty.error(requireContext(), "Network error: " + t.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private void uploadAvatar(Uri imageUri) {
        android.util.Log.d("EditProfile", "Starting avatar upload process");
        android.util.Log.d("EditProfile", "Image URI: " + imageUri);

        try {
            binding.loadingIndicator.setVisibility(View.VISIBLE);
            binding.changeAvatarButton.setEnabled(false);

            // Create MultipartBody.Part
            File file = createTempFileFromUri(imageUri);
            android.util.Log.d("EditProfile", "File path: " + file.getAbsolutePath());
            android.util.Log.d("EditProfile", "File size: " + file.length());

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(requireContext().getContentResolver().getType(imageUri)),
                    file
            );

            MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            // Create other request parts
            RequestBody action = RequestBody.create(MediaType.parse("text/plain"), "update_avatar");
            RequestBody id = RequestBody.create(MediaType.parse("text/plain"), currentUser.getId());

            // Make API call
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ResponseBody> call = apiService.updateAvatar(action, id, avatarPart);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    binding.loadingIndicator.setVisibility(View.GONE);
                    binding.changeAvatarButton.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String jsonResponse = response.body().string();
                            android.util.Log.d("EditProfile", "Response: " + jsonResponse);

                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getInt("status") == 1) {
                                String avatarPath = jsonObject.getString("avatar");
                                Log.d("EditProfile", "Avatar path: " + jsonResponse);
                                updateAvatarInUI(avatarPath);
                                Toasty.success(requireContext(), "Avatar updated successfully", Toasty.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toasty.error(requireContext(), message, Toasty.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            android.util.Log.e("EditProfile", "Error parsing response", e);
                            Toasty.error(requireContext(), "Error updating avatar", Toasty.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    binding.loadingIndicator.setVisibility(View.GONE);
                    binding.changeAvatarButton.setEnabled(true);
                    android.util.Log.e("EditProfile", "Upload failed", t);
                    Toasty.error(requireContext(), "Upload failed: " + t.getMessage(), Toasty.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.changeAvatarButton.setEnabled(true);
            android.util.Log.e("EditProfile", "Error preparing upload", e);
            Toasty.error(requireContext(), "Error preparing file: " + e.getMessage(), Toasty.LENGTH_SHORT).show();
        }
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = requireContext().getContentResolver();
        String fileExtension = contentResolver.getType(uri).split("/")[1]; // e.g., "jpg" or "png"

        File tempFile = File.createTempFile("avatar_", "." + fileExtension, requireContext().getCacheDir());
        tempFile.deleteOnExit();

        try (
                InputStream inputStream = contentResolver.openInputStream(uri);
                OutputStream outputStream = new FileOutputStream(tempFile)
        ) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead); // Corrected write method call
            }
            outputStream.flush();
        }

        return tempFile;
    }


    private void updateAvatarInUI(String avatarPath) {
        String avatarUrl = "https://mobile2.ndp.my.id/" + avatarPath;
        Glide.with(requireContext())
                .load(avatarUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .circleCrop()
                .into(binding.avatarImage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
