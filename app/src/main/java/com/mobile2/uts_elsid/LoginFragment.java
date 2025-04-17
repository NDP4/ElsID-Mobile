package com.mobile2.uts_elsid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobile2.uts_elsid.api.ApiClient;
import com.mobile2.uts_elsid.api.ApiService;
import com.mobile2.uts_elsid.api.LoginResponse;
import com.mobile2.uts_elsid.utils.SessionManager;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private TextInputEditText emailInput, passwordInput;
    private MaterialButton loginButton;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        sessionManager = new SessionManager(requireContext());

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
            return view;
        }

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toasty.warning(requireContext(), "Please fill all fields", Toasty.LENGTH_SHORT).show();
                return;
            }

            performLogin(email, password);
        });

        return view;
    }

    private void performLogin(String email, String password) {
        boolean hasError = false;

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
        Call<LoginResponse> call = apiService.login("login", email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.getStatus() == 1) {
                        sessionManager.saveLoginSession(loginResponse);
                        Toasty.success(requireContext(), "Login successful! Welcome back", Toasty.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                        getActivity().finish();
                    } else {
                        Toasty.error(requireContext(), "Login failed: " + loginResponse.getMessage(), Toasty.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toasty.error(requireContext(), "Network error: Unable to connect to server", Toasty.LENGTH_SHORT).show();
            }
        });
    }
}