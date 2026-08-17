package in.ivinnovations.healthtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import in.ivinnovations.healthtracker.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvEmail;

    private MaterialButton btnUpdateDetails;
    private MaterialButton btnWeightHistory;
    private MaterialButton btnLogout;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupListeners();
        loadUserInfo();
    }

    private void initializeViews() {

        btnBack = findViewById(R.id.btnBack);
        tvEmail = findViewById(R.id.tvEmail);

        btnUpdateDetails =
                findViewById(R.id.btnUpdateDetails);

        btnWeightHistory =
                findViewById(R.id.btnWeightHistory);

        btnLogout =
                findViewById(R.id.btnLogout);
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnUpdateDetails.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            UpdateDetailsActivity.class
                    )
            );
        });

        btnWeightHistory.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            WeightHistoryActivity.class
                    )
            );
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserInfo() {

        if (mAuth.getCurrentUser() != null) {

            String email =
                    mAuth.getCurrentUser().getEmail();

            tvEmail.setText(
                    email != null ? email : ""
            );
        }
    }

    private void logout() {

        mAuth.signOut();

        Intent intent =
                new Intent(
                        SettingsActivity.this,
                        LoginActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}