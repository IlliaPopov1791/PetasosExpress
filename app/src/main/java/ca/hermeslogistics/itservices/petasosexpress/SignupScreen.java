package ca.hermeslogistics.itservices.petasosexpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class SignupScreen extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button loginButton;
    private Button signupButton;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.signup_screen);
        } else {
            setContentView(R.layout.signup_screen_landscape);
        }

        //Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        //Initialize views
        firstNameEditText = findViewById(R.id.editTextFirstName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        emailEditText = findViewById(R.id.editTextEmail);
        phoneEditText = findViewById(R.id.editTextPhoneNumber);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        signupButton = findViewById(R.id.buttonSignUp);
        loginButton = findViewById(R.id.buttonLogin);
        rememberMeCheckBox = findViewById(R.id.remember);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Getting user input
                final String email = emailEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();
                final String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                final String firstName = firstNameEditText.getText().toString().trim();
                final String lastName = lastNameEditText.getText().toString().trim();
                final String phone = phoneEditText.getText().toString().trim();

                //Checking if any field is empty
                if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                        TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                        TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignupScreen.this, R.string.all_fields_filled, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Checking if the email is in correct format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignupScreen.this, R.string.invalid_email_format, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Checking if passwords match
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupScreen.this, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Checking if the password is complex enough
                String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
                if (!password.matches(passwordPattern)) {
                    Toast.makeText(SignupScreen.this, R.string.invalid_password_format1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignupScreen.this, R.string.invalid_password_format2, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Creating user in Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //Success
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("RememberMe", rememberMeCheckBox.isChecked());
                                    editor.apply();
                                    addUserToFirestore(email, firstName, lastName, phone);
                                    Toast.makeText(SignupScreen.this, R.string.registration_in_process,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    //Check if email is used.
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(SignupScreen.this, R.string.email_already_in_use, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignupScreen.this, R.string.authentication_failed,
                                                Toast.LENGTH_SHORT).show();
                                    }
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

    private void addUserToFirestore(String email, String firstName, String lastName, String phone) {
        //Creating a new user record with a first name, last name, email, and phone
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("phone", Long.parseLong(phone));

        //Adding a new document with the email as the ID to link with Authentication
        db.collection("userInfo").document(email)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    //Success
                    Toast.makeText(SignupScreen.this, (R.string.registration_successful), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    //Fail
                    Toast.makeText(SignupScreen.this, (R.string.registration_failed_please_try_again), Toast.LENGTH_SHORT).show();
                });
    }
}
