package in.ivinnovations.healthtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import in.ivinnovations.healthtracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private MaterialButton btnWeightHistory;

    private TextView tvGreeting;
    private TextView tvBmiValue;
    private TextView tvBmiCategory;
    private TextView tvWeight;
    private TextView tvHeight;

    private MaterialButton btnUpdateDetails;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        loadUserData();
        setupListeners();
    }

    private void initializeViews() {

        tvGreeting = findViewById(R.id.tvGreeting);
        tvBmiValue = findViewById(R.id.tvBmiValue);
        tvBmiCategory = findViewById(R.id.tvBmiCategory);
        tvWeight = findViewById(R.id.tvWeight);
        tvHeight = findViewById(R.id.tvHeight);

        btnWeightHistory =
                findViewById(R.id.btnWeightHistory);

        btnUpdateDetails = findViewById(R.id.btnUpdateDetails);
    }

    private void setupListeners() {

        btnUpdateDetails.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    UpdateDetailsActivity.class
            );

            startActivity(intent);
        });

        btnWeightHistory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    WeightHistoryActivity.class
            );

            startActivity(intent);
        });
    }

    private void loadUserData() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "User session not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        String uid = currentUser.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        displayUserData(documentSnapshot);

                    } else {

                        Toast.makeText(
                                this,
                                "User data not found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load data: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void displayUserData(
            DocumentSnapshot document
    ) {

        String name = document.getString("name");

        if (name != null && !name.isEmpty()) {

            tvGreeting.setText(
                    "Good morning, " + name + " 👋"
            );
        }

        Double weight = document.getDouble("weight");
        Double height = document.getDouble("height");

        String weightUnit =
                document.getString("weightUnit");

        String heightUnit =
                document.getString("heightUnit");

        if (weight != null) {

            tvWeight.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.1f %s",
                            weight,
                            weightUnit != null
                                    ? weightUnit
                                    : "KG"
                    )
            );
        }

        if (height != null) {

            tvHeight.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.1f %s",
                            height,
                            heightUnit != null
                                    ? heightUnit
                                    : "CM"
                    )
            );
        }

        calculateAndDisplayBMI(
                weight,
                height,
                weightUnit,
                heightUnit
        );
    }

    private void calculateAndDisplayBMI(
            Double weight,
            Double height,
            String weightUnit,
            String heightUnit
    ) {

        if (weight == null || height == null) {
            return;
        }

        // Convert weight to KG

        double weightKg = weight;

        if ("LBS".equals(weightUnit)) {

            weightKg = weight * 0.45359237;
        }

        // Convert height to meters

        double heightMeters;

        if ("CM".equals(heightUnit)) {

            heightMeters = height / 100.0;

        } else {

            heightMeters = height * 0.0254;
        }

        if (heightMeters <= 0) {
            return;
        }

        double bmi =
                weightKg /
                        (heightMeters * heightMeters);

        String category =
                getBmiCategory(bmi);

        tvBmiValue.setText(
                String.format(
                        Locale.getDefault(),
                        "%.1f",
                        bmi
                )
        );

        tvBmiCategory.setText(category);
    }

    private String getBmiCategory(double bmi) {

        if (bmi < 18.5) {

            return "Underweight";

        } else if (bmi < 25.0) {

            return "Normal Weight";

        } else if (bmi < 30.0) {

            return "Overweight";

        } else {

            return "Obesity";
        }
    }
}