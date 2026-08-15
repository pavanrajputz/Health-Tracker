package in.ivinnovations.healthtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

import in.ivinnovations.healthtracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    private MaterialButton btnCreateAccount;

    private ImageButton btnBack;
    private TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnBack = findViewById(R.id.btnBack);
        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        tvSignIn.setOnClickListener(v -> finish());

        btnCreateAccount.setOnClickListener(v -> validateRegistration());
    }

    private void validateRegistration() {

        clearErrors();

        String name = getText(etName);
        String email = getText(etEmail);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);

        boolean isValid = true;

        // Name validation
        if (TextUtils.isEmpty(name)) {

            tilName.setError("Please enter your name");
            isValid = false;

        }

        // Email validation
        if (TextUtils.isEmpty(email)) {

            tilEmail.setError("Please enter your email");

            isValid = false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            tilEmail.setError("Please enter a valid email");

            isValid = false;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {

            tilPassword.setError("Please enter a password");

            isValid = false;

        } else if (password.length() < 6) {

            tilPassword.setError("Password must be at least 6 characters");

            isValid = false;
        }

        // Confirm password validation
        if (TextUtils.isEmpty(confirmPassword)) {

            tilConfirmPassword.setError("Please confirm your password");

            isValid = false;

        } else if (!password.equals(confirmPassword)) {

            tilConfirmPassword.setError("Passwords do not match");

            isValid = false;
        }

        if (isValid) {

            createAccount(name, email, password);
        }
    }

    private void clearErrors() {

        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private String getText(TextInputEditText editText) {

        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }

    private void createAccount(
            String name,
            String email,
            String password
    ) {

        btnCreateAccount.setEnabled(false);
        btnCreateAccount.setText("Creating Account...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {

                            saveUserProfile(
                                    user.getUid(),
                                    name,
                                    email
                            );
                        }

                    } else {

                        btnCreateAccount.setEnabled(true);
                        btnCreateAccount.setText("Create Account");

                        String errorMessage = "Registration failed";

                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }

                        Toast.makeText(
                                RegisterActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void saveUserProfile(
            String uid,
            String name,
            String email
    ) {

        Map<String, Object> userData = new HashMap<>();

        userData.put("name", name);
        userData.put("email", email);
        userData.put("createdAt",
                System.currentTimeMillis());

        db.collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Account created successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    openUserDetails();

                })
                .addOnFailureListener(e -> {

                    btnCreateAccount.setEnabled(true);
                    btnCreateAccount.setText("Create Account");

                    Toast.makeText(
                            RegisterActivity.this,
                            "Could not save user profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void openUserDetails() {

        Intent intent = new Intent(
                RegisterActivity.this,
                UserDetailsActivity.class
        );

        startActivity(intent);

        finish();
    }
}
