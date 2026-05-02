package com.example.isisgamecollector.UI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.isisgamecollector.R;
import com.example.isisgamecollector.UI.database.Repository;
import com.example.isisgamecollector.UI.entities.User;

import java.util.regex.Pattern;

public class AccountCreationActivity extends AppCompatActivity {
    private EditText editUsername;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Repository repository;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_creation);

        repository = new Repository(getApplication());
        editUsername = findViewById(R.id.username_create);
        editEmail = findViewById(R.id.email_create);
        editPassword = findViewById(R.id.password_create);
        editConfirmPassword = findViewById(R.id.password_confirm);
        Button registerButton = findViewById(R.id.button_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account_creation_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int leftPadding = (int) (32 * getResources().getDisplayMetrics().density);
            int rightPadding = (int) (32 * getResources().getDisplayMetrics().density);
            int bottomPadding = (int) (32 * getResources().getDisplayMetrics().density);
            v.setPadding(systemBars.left + leftPadding, systemBars.top, systemBars.right + rightPadding, systemBars.bottom + bottomPadding);
            return insets;
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString();
                String confirmPassword = editConfirmPassword.getText().toString();

                if (!validateUsername(username)) {
                    Toast.makeText(AccountCreationActivity.this, "Username must be at least 5 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validateEmail(email)) {
                    Toast.makeText(AccountCreationActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validatePassword(password)) {
                    Toast.makeText(AccountCreationActivity.this, "Password does not meet requirements", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(AccountCreationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (repository.getUserByUsername(username) != null) {
                    Toast.makeText(AccountCreationActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (repository.getUserByEmail(email) != null) {
                    Toast.makeText(AccountCreationActivity.this, "An account with this email already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User(0, username, password, email);
                repository.insert(newUser);
                Toast.makeText(AccountCreationActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateUsername(String username) {
        return username != null && username.length() >= 5;
    }

    private boolean validateEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        if (password == null || password.length() < 8) return false;

        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find();

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
}
