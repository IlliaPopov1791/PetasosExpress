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
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3000; // 3 seconds
    private FirebaseAuth mAuth;
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

        if (currentUser != null && isRemembered || account != null) {
            currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        startNextActivityAfterDelay(MainActivity.class);
                    } else {
                        startNextActivityAfterDelay(LoginScreen.class);
                    }
                }
            });
        } else {
            startNextActivityAfterDelay(LoginScreen.class);
        }
    }

    private void startNextActivityAfterDelay(final Class<?> activityToStart) {
        new Handler().postDelayed(() -> startNextActivityImmediately(activityToStart), SPLASH_DURATION);
    }

    private void startNextActivityImmediately(final Class<?> activityToStart) {
        Intent intent = new Intent(SplashActivity.this, activityToStart);
        startActivity(intent);
        finish();
    }
}
