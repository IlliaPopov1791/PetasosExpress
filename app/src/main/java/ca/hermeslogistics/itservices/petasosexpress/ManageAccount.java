package ca.hermeslogistics.itservices.petasosexpress;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class ManageAccount extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button btnSave;
    private EditText inputEmail, inputFirstName, inputLastName, inputPhone;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load the appropriate layout based on orientation
        View view;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.account_manage, container, false);
        } else {
            view = inflater.inflate(R.layout.account_manage_landscape, container, false);
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        inputEmail = view.findViewById(R.id.inputEmail);
        inputFirstName = view.findViewById(R.id.inputFirstName);
        inputLastName = view.findViewById(R.id.inputLastName);
        inputPhone = view.findViewById(R.id.inputPhone);

        loadUserData();

        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        return view;
    }

    private void saveUserData() {
        String newFirstName = inputFirstName.getText().toString().trim();
        String newLastName = inputLastName.getText().toString().trim();
        String newPhone = inputPhone.getText().toString().trim();

        // Validate first name
        if (newFirstName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your first name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate last name
        if (newLastName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your last name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove non-numeric characters from phone number
        newPhone = newPhone.replaceAll("[^0-9]", "");

        // Validate phone number length (assuming a valid phone number should have 10 digits)
        if (newPhone.length() != 10) {
            Toast.makeText(getContext(), R.string.invalid_phone_format, Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with saving user data
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                db.collection("userInfo").document(userEmail)
                        .update("firstName", newFirstName,
                                "lastName", newLastName,
                                "phone", Long.parseLong(newPhone))
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error updating data", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }


    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                inputEmail.setText(userEmail); // Set email in the email field
                db.collection("userInfo").document(userEmail).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        Long phone = documentSnapshot.getLong("phone");

                        inputFirstName.setText(firstName != null ? firstName : "");
                        inputLastName.setText(lastName != null ? lastName : "");
                        inputPhone.setText(phone != null ? String.valueOf(phone) : "");
                    }
                }).addOnFailureListener(e -> {

                });
            }
        }
    }
}
