package in.ivinnovations.healthtracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import in.ivinnovations.healthtracker.activities.DashboardActivity;
import in.ivinnovations.healthtracker.activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        checkUserSession();
    }

    private void checkUserSession() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            Intent intent = new Intent(
                    MainActivity.this,
                    DashboardActivity.class
            );

            startActivity(intent);

        } else {

            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
        }

        finish();
    }
}