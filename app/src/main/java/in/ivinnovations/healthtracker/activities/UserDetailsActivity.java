package in.ivinnovations.healthtracker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import in.ivinnovations.healthtracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {

    private TextInputEditText etDateOfBirth;
    private TextInputEditText etWeight;
    private TextInputEditText etHeight;

    private TextInputLayout tilDateOfBirth;
    private TextInputLayout tilWeight;
    private TextInputLayout tilHeight;

    private RadioGroup rgGender;

    private MaterialAutoCompleteTextView spinnerWeightUnit;
    private MaterialAutoCompleteTextView spinnerHeightUnit;

    private MaterialButton btnContinue;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        setContentView(R.layout.activity_user_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupUnitDropdowns();
        setupListeners();
    }

    private void initializeViews() {

        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);

        tilDateOfBirth = findViewById(R.id.tilDateOfBirth);
        tilWeight = findViewById(R.id.tilWeight);
        tilHeight = findViewById(R.id.tilHeight);

        rgGender = findViewById(R.id.rgGender);

        spinnerWeightUnit = findViewById(R.id.spinnerWeightUnit);
        spinnerHeightUnit = findViewById(R.id.spinnerHeightUnit);

        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupUnitDropdowns() {

        String[] weightUnits = {"KG", "LBS"};

        ArrayAdapter<String> weightAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        weightUnits
                );

        spinnerWeightUnit.setAdapter(weightAdapter);

        String[] heightUnits = {"CM", "Inches"};

        ArrayAdapter<String> heightAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        heightUnits
                );

        spinnerHeightUnit.setAdapter(heightAdapter);
    }

    private void setupListeners() {

        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        btnContinue.setOnClickListener(v -> validateDetails());
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    selectedDate = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                    );

                    etDateOfBirth.setText(selectedDate);

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMaxDate(
                System.currentTimeMillis()
        );

        dialog.show();
    }

    private void validateDetails() {

        hideKeyboard();

        clearErrors();

        String weightText = getText(etWeight);
        String heightText = getText(etHeight);

        boolean isValid = true;

        // Date validation

        if (selectedDate.isEmpty()) {

            tilDateOfBirth.setError(
                    "Please select your date of birth"
            );

            isValid = false;
        }

        // Gender validation

        if (rgGender.getCheckedRadioButtonId() == -1) {

            Toast.makeText(
                    this,
                    "Please select your gender",
                    Toast.LENGTH_SHORT
            ).show();

            isValid = false;
        }

        // Weight validation

        if (weightText.isEmpty()) {

            tilWeight.setError(
                    "Please enter your weight"
            );

            isValid = false;

        } else {

            try {

                double weight = Double.parseDouble(weightText);

                if (weight <= 0) {

                    tilWeight.setError(
                            "Weight must be greater than 0"
                    );

                    isValid = false;
                }

            } catch (NumberFormatException e) {

                tilWeight.setError(
                        "Please enter a valid weight"
                );

                isValid = false;
            }
        }

        // Height validation

        if (heightText.isEmpty()) {

            tilHeight.setError(
                    "Please enter your height"
            );

            isValid = false;

        } else {

            try {

                double height = Double.parseDouble(heightText);

                if (height <= 0) {

                    tilHeight.setError(
                            "Height must be greater than 0"
                    );

                    isValid = false;
                }

            } catch (NumberFormatException e) {

                tilHeight.setError(
                        "Please enter a valid height"
                );

                isValid = false;
            }
        }

        if (isValid) {

            saveUserDetails();
        }
    }

    private void saveUserDetails() {

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "User session not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        RadioButton selectedGender =
                findViewById(rgGender.getCheckedRadioButtonId());

        String gender = selectedGender.getText().toString();

        String weightUnit =
                spinnerWeightUnit.getText().toString();

        String heightUnit =
                spinnerHeightUnit.getText().toString();

        double weight =
                Double.parseDouble(getText(etWeight));

        double height =
                Double.parseDouble(getText(etHeight));

        Map<String, Object> details = new HashMap<>();

        details.put("dateOfBirth", selectedDate);
        details.put("gender", gender);
        details.put("weight", weight);
        details.put("weightUnit", weightUnit);
        details.put("height", height);
        details.put("heightUnit", heightUnit);

        btnContinue.setEnabled(false);
        btnContinue.setText("Saving...");

        db.collection("users")
                .document(uid)
                .update(details)
                .addOnSuccessListener(unused -> {

                    saveWeightHistory(
                            weight,
                            height,
                            weightUnit,
                            heightUnit
                    );

                })
                .addOnFailureListener(e -> {

                    btnContinue.setEnabled(true);
                    btnContinue.setText("Continue");

                    Toast.makeText(
                            this,
                            "Failed to save details: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void saveWeightHistory(
            double weight,
            double height,
            String weightUnit,
            String heightUnit
    ) {

        if (mAuth.getCurrentUser() == null) {
            return;
        }

        double weightKg = weight;

        if ("LBS".equals(weightUnit)) {
            weightKg = weight * 0.45359237;
        }

        Map<String, Object> history = new HashMap<>();

        history.put("weight", weight);
        history.put("weightKg", weightKg);
        history.put("unit", weightUnit);
        history.put(
                "timestamp",
                System.currentTimeMillis()
        );

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("weightHistory")
                .add(history)
                .addOnSuccessListener(documentReference -> {

                    calculateAndOpenBMI(
                            weight,
                            height,
                            weightUnit,
                            heightUnit
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Could not save weight history",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Even if history fails, the main
                    // user details were already saved.
                    calculateAndOpenBMI(
                            weight,
                            height,
                            weightUnit,
                            heightUnit
                    );
                });
    }

    private void calculateAndOpenBMI(
            double weight,
            double height,
            String weightUnit,
            String heightUnit
    ) {

        double weightKg = weight;
        double heightMeters;

        if ("LBS".equals(weightUnit)) {

            weightKg = weight * 0.45359237;
        }

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

        Intent intent = new Intent(
                UserDetailsActivity.this,
                DashboardActivity.class
        );

        startActivity(intent);

        finish();
    }

    private String getText(TextInputEditText editText) {

        if (editText.getText() == null) {
            return "";
        }

        return editText.getText()
                .toString()
                .trim();
    }

    private void clearErrors() {

        tilDateOfBirth.setError(null);
        tilWeight.setError(null);
        tilHeight.setError(null);
    }

    private void hideKeyboard() {

        android.view.View view = getCurrentFocus();

        if (view != null) {

            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(
                        view.getWindowToken(),
                        0
                );
            }
        }
    }
}