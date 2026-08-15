package in.ivinnovations.healthtracker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.ivinnovations.healthtracker.R;
import in.ivinnovations.healthtracker.views.WeightChartView;

public class WeightHistoryActivity extends AppCompatActivity {

    private WeightChartView weightChart;
    private LinearLayout historyContainer;
    private TextView tvNoData;
    private ImageButton btnBack;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final List<Float> weights = new ArrayList<>();
    private final List<String> labels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weight_history);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupListeners();
        loadWeightHistory();
    }

    private void initializeViews() {

        weightChart = findViewById(R.id.weightChart);
        historyContainer = findViewById(R.id.historyContainer);
        tvNoData = findViewById(R.id.tvNoData);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadWeightHistory() {

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        // Seven days ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6);

        long sevenDaysAgo = calendar.getTimeInMillis();

        db.collection("users")
                .document(uid)
                .collection("weightHistory")
                .whereGreaterThanOrEqualTo(
                        "timestamp",
                        sevenDaysAgo
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showNoData();

                        return;
                    }

                    List<DocumentSnapshot> documents =
                            new ArrayList<>(
                                    querySnapshot.getDocuments()
                            );

                    // Newest first
                    Collections.sort(
                            documents,
                            (a, b) -> Long.compare(
                                    getTimestamp(b),
                                    getTimestamp(a)
                            )
                    );

                    prepareChartData(documents);
                    displayRecentEntries(documents);

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load weight history: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private long getTimestamp(DocumentSnapshot document) {

        Long timestamp =
                document.getLong("timestamp");

        return timestamp != null ? timestamp : 0;
    }

    private void prepareChartData(
            List<DocumentSnapshot> documents
    ) {

        weights.clear();
        labels.clear();

        // Chart should be oldest → newest
        Collections.reverse(documents);

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd/MM",
                        Locale.getDefault()
                );

        for (DocumentSnapshot document : documents) {

            Double weightKg =
                    document.getDouble("weightKg");

            Long timestamp =
                    document.getLong("timestamp");

            if (weightKg == null || timestamp == null) {
                continue;
            }

            weights.add(weightKg.floatValue());

            labels.add(
                    dateFormat.format(
                            new Date(timestamp)
                    )
            );
        }

        if (!weights.isEmpty()) {

            weightChart.setData(
                    weights,
                    labels
            );
        }
    }

    private void displayRecentEntries(
            List<DocumentSnapshot> documents
    ) {

        historyContainer.removeAllViews();

        // Show newest entries first
        for (DocumentSnapshot document : documents) {

            Double weight =
                    document.getDouble("weight");

            String unit =
                    document.getString("unit");

            Long timestamp =
                    document.getLong("timestamp");

            if (weight == null || timestamp == null) {
                continue;
            }

            addHistoryRow(
                    weight,
                    unit,
                    timestamp
            );
        }
    }

    private void addHistoryRow(
            double weight,
            String unit,
            long timestamp
    ) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                android.view.Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                16,
                16,
                16,
                16
        );

        TextView dateText =
                new TextView(this);

        dateText.setText(
                formatDate(timestamp)
        );

        dateText.setTextSize(15);

        dateText.setTextColor(
                getColor(R.color.text_secondary)
        );

        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        row.addView(
                dateText,
                dateParams
        );

        TextView weightText =
                new TextView(this);

        weightText.setText(
                String.format(
                        Locale.getDefault(),
                        "%.1f %s",
                        weight,
                        unit != null
                                ? unit
                                : "KG"
                )
        );

        weightText.setTextSize(16);

        weightText.setTextColor(
                getColor(R.color.text_primary)
        );

        weightText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        row.addView(weightText);

        historyContainer.addView(row);

        View divider =
                new View(this);

        divider.setBackgroundColor(
                getColor(R.color.divider)
        );

        historyContainer.addView(
                divider,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                )
        );
    }

    private String formatDate(long timestamp) {

        Calendar entry =
                Calendar.getInstance();

        entry.setTimeInMillis(timestamp);

        Calendar today =
                Calendar.getInstance();

        if (isSameDay(entry, today)) {
            return "Today";
        }

        today.add(
                Calendar.DAY_OF_YEAR,
                -1
        );

        if (isSameDay(entry, today)) {
            return "Yesterday";
        }

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                );

        return format.format(
                new Date(timestamp)
        );
    }

    private boolean isSameDay(
            Calendar first,
            Calendar second
    ) {

        return first.get(
                Calendar.YEAR
        ) == second.get(
                Calendar.YEAR
        )
                &&
                first.get(
                        Calendar.DAY_OF_YEAR
                ) == second.get(
                        Calendar.DAY_OF_YEAR
                );
    }

    private void showNoData() {

        weightChart.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);
        historyContainer.removeAllViews();
    }
}