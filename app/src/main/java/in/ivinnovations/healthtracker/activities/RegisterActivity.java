package in.ivinnovations.healthtracker.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import in.ivinnovations.healthtracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

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

        // Firebase registration will be added next.

        Toast.makeText(
                this,
                "Validation successful",
                Toast.LENGTH_SHORT
        ).show();
    }
}
