package com.mobile2.uts_elsid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobile2.uts_elsid.api.ApiClient;
import com.mobile2.uts_elsid.api.ApiService;
import com.mobile2.uts_elsid.api.RegisterResponse;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private TextInputEditText nameInput, emailInput, passwordInput;
    private MaterialButton registerButton;

    private void performRegister(String fullname, String email, String password) {
        boolean hasError = false;

        // Fullname validation
        if (fullname.isEmpty()) {
            Toasty.warning(requireContext(), "Full name field is required", Toasty.LENGTH_SHORT).show();
            hasError = true;
        } else if (fullname.length() < 3) {
            Toasty.warning(requireContext(), "Full name must be at least 3 characters", Toasty.LENGTH_SHORT).show();
            hasError = true;
        }

        // Email validation
        if (email.isEmpty()) {
            Toasty.warning(requireContext(), "Email field is required", Toasty.LENGTH_SHORT).show();
            hasError = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toasty.warning(requireContext(), "Please enter a valid email address", Toasty.LENGTH_SHORT).show();
            hasError = true;
        }

        // Password validation
        if (password.isEmpty()) {
            Toasty.warning(requireContext(), "Password field is required", Toasty.LENGTH_SHORT).show();
            hasError = true;
        } else if (password.length() < 6) {
            Toasty.warning(requireContext(), "Password must be at least 6 characters", Toasty.LENGTH_SHORT).show();
            hasError = true;
        }

        if (hasError) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegisterResponse> call = apiService.register("register", fullname, email, password);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.getStatus() == 1) {
                        Toasty.success(requireContext(), "Registration successful! Please login", Toasty.LENGTH_SHORT).show();
                        ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
                        viewPager.setCurrentItem(0);
                    } else {
                        Toasty.error(requireContext(), "Registration failed: " + registerResponse.getMessage(), Toasty.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toasty.error(requireContext(), "Network error: Unable to connect to server", Toasty.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        nameInput = view.findViewById(R.id.nameInput);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            performRegister(name, email, password);
        });

        return view;
    }
}