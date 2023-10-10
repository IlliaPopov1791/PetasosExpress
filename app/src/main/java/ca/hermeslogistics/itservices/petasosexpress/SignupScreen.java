package ca.hermeslogistics.itservices.petasosexpress;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

public class SignupScreen extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.signup_screen);
        } else {
            setContentView(R.layout.signup_screen_landscape);
        }

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        signupButton = findViewById(R.id.buttonSignUp);
        loginButton = findViewById(R.id.buttonLogin);


        // Implement a click listener for the signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                // Create user in Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign-up successful
                                    Toast.makeText(SignupScreen.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupScreen.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Sign-up failed
                                    Toast.makeText(SignupScreen.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupScreen.this, LoginScreen.class);
                startActivity(intent);
            }
        });
    }
}
