package com.example.splitexpense.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.splitexpense.R;
import com.example.splitexpense.ui.MainActivity;
import com.example.splitexpense.util.SessionManager;
import com.example.splitexpense.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private Button registerButton;
    private TextView loginLink;
    
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize UI components
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);

        // Set click listeners
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to login screen
                finish();
            }
        });
        
        // Observe registration result
        authViewModel.getRegistrationResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result != null) {
                    Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
                    
                    if (result.equals("Registration successful")) {
                        // Return to login screen after successful registration
                        finish();
                    }
                }
            }
        });
    }

    private void attemptRegistration() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate input (basic validation, more is done in ViewModel)
        if (username.isEmpty()) {
            usernameInput.setError("Username cannot be empty");
            return;
        }

        if (email.isEmpty()) {
            emailInput.setError("Email cannot be empty");
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password cannot be empty");
            return;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError("Please confirm your password");
            return;
        }

        // Attempt registration
        authViewModel.registerUser(username, email, password, confirmPassword);
    }
}
