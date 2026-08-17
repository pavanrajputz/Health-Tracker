package in.ivinnovations.healthtracker.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import in.ivinnovations.healthtracker.R;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;

    private MaterialButton btnLogin;
    private MaterialButton btnGoogle;

    private TextView tvForgotPassword;
    private TextView tvCreateAccount;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        configureGoogleSignIn();
        registerGoogleLauncher();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
    }

    private void configureGoogleSignIn() {

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleSignInClient =
                GoogleSignIn.getClient(this, gso);
    }

    private void registerGoogleLauncher() {

        googleSignInLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if (result.getData() == null) {

                                Toast.makeText(
                                        this,
                                        "Google Sign-In cancelled",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            try {

                                var task =
                                        GoogleSignIn.getSignedInAccountFromIntent(
                                                result.getData()
                                        );

                                var account =
                                        task.getResult(ApiException.class);

                                firebaseAuthWithGoogle(
                                        account.getIdToken()
                                );

                            } catch (ApiException e) {

                                Log.e(
                                        "GoogleSignIn",
                                        "Google Sign-In failed",
                                        e
                                );

                                Toast.makeText(
                                        this,
                                        "Google Sign-In failed",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void firebaseAuthWithGoogle(String idToken) {

        if (idToken == null) {

            Toast.makeText(
                    this,
                    "Google authentication token missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        AuthCredential credential =
                GoogleAuthProvider.getCredential(
                        idToken,
                        null
                );

        String email = getText(etEmail);
        String password = getText(etPassword);

        if (email.isEmpty()) {
            tilEmail.setError("Please enter your email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email");
            return;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Please enter your password");
            return;
        }

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user =
                                mAuth.getCurrentUser();

                        if (user != null) {

                            checkUserProfile(user);
                        }

                    } else {

                        String message =
                                "Google authentication failed";

                        if (task.getException() != null) {
                            message =
                                    task.getException().getMessage();
                        }

                        Toast.makeText(
                                this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void checkUserProfile(FirebaseUser user) {

        String uid = user.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        openDashboard();

                    } else {

                        createGoogleUserProfile(user);

                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to load profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void createGoogleUserProfile(FirebaseUser user) {

        java.util.Map<String, Object> userData =
                new java.util.HashMap<>();

        userData.put(
                "name",
                user.getDisplayName() != null
                        ? user.getDisplayName()
                        : ""
        );

        userData.put(
                "email",
                user.getEmail() != null
                        ? user.getEmail()
                        : ""
        );

        userData.put(
                "createdAt",
                System.currentTimeMillis()
        );

        db.collection("users")
                .document(user.getUid())
                .set(userData)
                .addOnSuccessListener(unused -> {

                    openUserDetails();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Could not create profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void openUserDetails() {

        Intent intent =
                new Intent(
                        LoginActivity.this,
                        UserDetailsActivity.class
                );

        startActivity(intent);
    }

    private void openDashboard() {

        Intent intent =
                new Intent(
                        LoginActivity.this,
                        DashboardActivity.class
                );

        startActivity(intent);

        finish();
    }

    private void setupListeners() {

        btnLogin.setOnClickListener(v -> {

            // Firebase login will be implemented next.

        });

        btnGoogle.setOnClickListener(v -> {

            Intent signInIntent =
                    googleSignInClient.getSignInIntent();

            googleSignInLauncher.launch(signInIntent);
        });

        tvForgotPassword.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);
        });

        tvCreateAccount.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }
}
