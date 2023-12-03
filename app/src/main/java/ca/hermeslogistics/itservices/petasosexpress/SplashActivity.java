package ca.hermeslogistics.itservices.petasosexpress;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GetTokenResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/*
 * SplashActivity: This activity serves as the splash screen and handles the initial authentication check.
 * Authors: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student IDs: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class SplashActivity extends AppCompatActivity {

    // Duration of the splash screen in milliseconds
    private static final long SPLASH_DURATION = 3000; // 3 seconds

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    // SharedPreferences for storing user preferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load the appropriate layout based on orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_splash);
        } else {
            setContentView(R.layout.splash_landscape);
        }

        // Check if user is already logged in and "Remember Me" is true
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        boolean isRemembered = sharedPreferences.getBoolean("RememberMe", false);

        // If the user is logged in and "Remember Me" is true, check the token
        if (currentUser != null && isRemembered || account != null) {
            currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        // If token retrieval is successful, start the MainActivity
                        startNextActivityAfterDelay(MainActivity.class);
                    } else {
                        // If token retrieval fails, start the LoginScreen
                        startNextActivityAfterDelay(LoginScreen.class);
                    }
                }
            });
        } else {
            // If the user is not logged in, start the LoginScreen
            startNextActivityAfterDelay(LoginScreen.class);
        }
    }

    // Start the next activity after a delay
    private void startNextActivityAfterDelay(final Class<?> activityToStart) {
        new Handler().postDelayed(() -> startNextActivityImmediately(activityToStart), SPLASH_DURATION);
    }

    // Start the next activity immediately
    private void startNextActivityImmediately(final Class<?> activityToStart) {
        Intent intent = new Intent(SplashActivity.this, activityToStart);
        startActivity(intent);
        finish(); // Finish the SplashActivity to prevent it from being revisited
    }
}
