package in.ivinnovations.healthtracker.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import in.ivinnovations.healthtracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputLayout tilEmail;
    private MaterialButton btnSendReset;
    private ImageButton btnBack;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {

        etEmail = findViewById(R.id.etEmail);
        tilEmail = findViewById(R.id.tilEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnSendReset.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {

        tilEmail.setError(null);

        String email = "";

        if (etEmail.getText() != null) {
            email = etEmail.getText()
                    .toString()
                    .trim();
        }

        if (email.isEmpty()) {

            tilEmail.setError(
                    "Please enter your email"
            );

            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            tilEmail.setError(
                    "Please enter a valid email"
            );

            return;
        }

        btnSendReset.setEnabled(false);
        btnSendReset.setText("Sending...");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    btnSendReset.setEnabled(true);
                    btnSendReset.setText("Send Reset Link");

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                this,
                                "Password reset email sent. Check your inbox.",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();

                    } else {

                        String message =
                                "Unable to send reset email.";

                        if (task.getException() != null) {

                            message =
                                    task.getException()
                                            .getMessage();
                        }

                        Toast.makeText(
                                this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}