package in.ivinnovations.healthtracker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import in.ivinnovations.healthtracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateDetailsActivity extends AppCompatActivity {

    private TextInputEditText etDateOfBirth;
    private TextInputEditText etWeight;
    private TextInputEditText etHeight;

    private TextInputLayout tilDateOfBirth;
    private TextInputLayout tilWeight;
    private TextInputLayout tilHeight;

    private RadioGroup rgGender;

    private RadioButton rbMale;
    private RadioButton rbFemale;
    private RadioButton rbOther;

    private MaterialAutoCompleteTextView spinnerWeightUnit;
    private MaterialAutoCompleteTextView spinnerHeightUnit;

    private MaterialButton btnSave;
    private ImageButton btnBack;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupDropdowns();
        setupListeners();
        loadExistingData();
    }

    private void initializeViews() {

        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);

        tilDateOfBirth = findViewById(R.id.tilDateOfBirth);
        tilWeight = findViewById(R.id.tilWeight);
        tilHeight = findViewById(R.id.tilHeight);

        rgGender = findViewById(R.id.rgGender);

        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);

        spinnerWeightUnit = findViewById(R.id.spinnerWeightUnit);
        spinnerHeightUnit = findViewById(R.id.spinnerHeightUnit);

        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupDropdowns() {

        spinnerWeightUnit.setSimpleItems(
                new String[]{"KG", "LBS"}
        );

        spinnerHeightUnit.setSimpleItems(
                new String[]{"CM", "Inches"}
        );
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        etDateOfBirth.setOnClickListener(
                v -> showDatePicker()
        );

        btnSave.setOnClickListener(
                v -> validateAndSave()
        );
    }

    private void loadExistingData() {

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    String dob =
                            document.getString("dateOfBirth");

                    String gender =
                            document.getString("gender");

                    String weightUnit =
                            document.getString("weightUnit");

                    String heightUnit =
                            document.getString("heightUnit");

                    Double weight =
                            document.getDouble("weight");

                    Double height =
                            document.getDouble("height");

                    if (dob != null) {
                        selectedDate = dob;
                        etDateOfBirth.setText(dob);
                    }

                    if (gender != null) {

                        if (gender.equals("Male")) {
                            rbMale.setChecked(true);
                        } else if (gender.equals("Female")) {
                            rbFemale.setChecked(true);
                        } else {
                            rbOther.setChecked(true);
                        }
                    }

                    if (weight != null) {
                        etWeight.setText(
                                String.valueOf(weight)
                        );
                    }

                    if (height != null) {
                        etHeight.setText(
                                String.valueOf(height)
                        );
                    }

                    if (weightUnit != null) {
                        spinnerWeightUnit.setText(
                                weightUnit,
                                false
                        );
                    }

                    if (heightUnit != null) {
                        spinnerHeightUnit.setText(
                                heightUnit,
                                false
                        );
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load details",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, day) -> {

                            selectedDate =
                                    String.format(
                                            Locale.getDefault(),
                                            "%04d-%02d-%02d",
                                            year,
                                            month + 1,
                                            day
                                    );

                            etDateOfBirth.setText(
                                    selectedDate
                            );
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

    private void validateAndSave() {

        clearErrors();

        String weightText =
                etWeight.getText() == null
                        ? ""
                        : etWeight.getText()
                        .toString()
                        .trim();

        String heightText =
                etHeight.getText() == null
                        ? ""
                        : etHeight.getText()
                        .toString()
                        .trim();

        if (selectedDate.isEmpty()) {
            tilDateOfBirth.setError(
                    "Please select your date of birth"
            );
            return;
        }

        if (rgGender.getCheckedRadioButtonId() == -1) {

            Toast.makeText(
                    this,
                    "Please select your gender",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (weightText.isEmpty()) {

            tilWeight.setError(
                    "Please enter your weight"
            );

            return;
        }

        if (heightText.isEmpty()) {

            tilHeight.setError(
                    "Please enter your height"
            );

            return;
        }

        double weight;
        double height;

        try {

            weight = Double.parseDouble(weightText);
            height = Double.parseDouble(heightText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (weight <= 0) {

            tilWeight.setError(
                    "Weight must be greater than 0"
            );

            return;
        }

        if (height <= 0) {

            tilHeight.setError(
                    "Height must be greater than 0"
            );

            return;
        }

        saveChanges(weight, height);
    }

    private void saveChanges(
            double weight,
            double height
    ) {

        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String uid =
                mAuth.getCurrentUser().getUid();

        RadioButton selected =
                findViewById(
                        rgGender.getCheckedRadioButtonId()
                );

        String gender =
                selected.getText().toString();

        String weightUnit =
                spinnerWeightUnit.getText()
                        .toString();

        String heightUnit =
                spinnerHeightUnit.getText()
                        .toString();

        Map<String, Object> updates =
                new HashMap<>();

        updates.put("dateOfBirth", selectedDate);
        updates.put("gender", gender);
        updates.put("weight", weight);
        updates.put("weightUnit", weightUnit);
        updates.put("height", height);
        updates.put("heightUnit", heightUnit);

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        db.collection("users")
                .document(uid)
                .update(updates)
                .addOnSuccessListener(unused -> {

                    saveWeightHistory(
                            weight,
                            weightUnit
                    );

                })
                .addOnFailureListener(e -> {

                    btnSave.setEnabled(true);
                    btnSave.setText("Save Changes");

                    Toast.makeText(
                            this,
                            "Failed to update details: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void saveWeightHistory(
            double weight,
            String weightUnit
    ) {

        if (mAuth.getCurrentUser() == null) {
            return;
        }

        double weightKg = weight;

        if ("LBS".equals(weightUnit)) {
            weightKg = weight * 0.45359237;
        }

        Map<String, Object> history =
                new HashMap<>();

        history.put("weight", weight);
        history.put("weightKg", weightKg);
        history.put("unit", weightUnit);
        history.put(
                "timestamp",
                System.currentTimeMillis()
        );

        String uid =
                mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("weightHistory")
                .add(history)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "Details updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    openDashboard();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Details updated, but history could not be saved",
                            Toast.LENGTH_LONG
                    ).show();

                    openDashboard();
                });
    }

    private void openDashboard() {

        Intent intent =
                new Intent(
                        this,
                        DashboardActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        startActivity(intent);

        finish();
    }

    private void clearErrors() {

        tilDateOfBirth.setError(null);
        tilWeight.setError(null);
        tilHeight.setError(null);
    }
}