package com.example.isisgamecollector.UI;

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

public class PasswordResetActivity extends AppCompatActivity {
    private EditText editEmail;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);

        repository = new Repository(getApplication());
        editEmail = findViewById(R.id.email_reset);
        Button resetButton = findViewById(R.id.button_reset);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.password_reset_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int leftPadding = (int) (32 * getResources().getDisplayMetrics().density);
            int rightPadding = (int) (32 * getResources().getDisplayMetrics().density);
            v.setPadding(systemBars.left + leftPadding, systemBars.top, systemBars.right + rightPadding, systemBars.bottom);
            return insets;
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    editEmail.setError("Email is required");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editEmail.setError("Please enter a valid email address");
                    return;
                }

                User user = repository.getUserByEmail(email);
                if (user != null) {
                    // Logic to send reset link would go here
                    Toast.makeText(PasswordResetActivity.this, "A reset link has been sent to " + email, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    editEmail.setError("No account found with this email");
                }
            }
        });
    }
}
