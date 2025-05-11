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
import com.example.splitexpense.data.entity.User;
import com.example.splitexpense.ui.MainActivity;
import com.example.splitexpense.util.SessionManager;
import com.example.splitexpense.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private Button loginButton;
    private TextView forgotPasswordText;
    private TextView registerLink;
    
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
        }

        // Initialize UI components
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordText = findViewById(R.id.forgot_password);
        registerLink = findViewById(R.id.register_link);

        // Set click listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to registration screen
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password
                Toast.makeText(LoginActivity.this, "Forgot password functionality will be implemented", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observe login result
        authViewModel.getLoginResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result != null) {
                    if (result.equals("Login successful")) {
                        // Create session
                        User user = authViewModel.getCurrentUser().getValue();
                        if (user != null) {
                            sessionManager.createLoginSession(user);
                            navigateToMainActivity();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        if (username.isEmpty()) {
            usernameInput.setError("Username cannot be empty");
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password cannot be empty");
            return;
        }

        // Attempt login
        authViewModel.loginUser(username, password);
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
