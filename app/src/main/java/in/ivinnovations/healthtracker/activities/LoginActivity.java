package in.ivinnovations.healthtracker.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import in.ivinnovations.healthtracker.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;

    private MaterialButton btnLogin;
    private MaterialButton btnGoogle;

    private TextView tvForgotPassword;
    private TextView tvCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
    }

    private void setupListeners() {

        btnLogin.setOnClickListener(v -> {

            // Firebase login will be implemented next.

        });

        btnGoogle.setOnClickListener(v -> {

            // Google authentication will be implemented next.

        });

        tvForgotPassword.setOnClickListener(v -> {

            // Password reset screen will be implemented next.

        });

        tvCreateAccount.setOnClickListener(v -> {

            // Signup screen will be implemented next.

        });
    }
}
