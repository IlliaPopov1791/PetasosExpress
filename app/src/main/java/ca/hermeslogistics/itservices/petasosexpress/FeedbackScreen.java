package ca.hermeslogistics.itservices.petasosexpress;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class FeedbackScreen extends Fragment {

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextComment;
    private RatingBar ratingBar;
    private Button buttonSubmit;

    private FirebaseFirestore db;

    public FeedbackScreen() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_screen, container, false);

        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextComment = view.findViewById(R.id.editTextComment);
        ratingBar = view.findViewById(R.id.ratingBar);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        return view;
    }

    private void submitFeedback() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String comment = editTextComment.getText().toString().trim();
        float rating = ratingBar.getRating();

        // Get the device model information
        String deviceModel = android.os.Build.MODEL;

        // Validate inputs
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            DisplayToast(getString(R.string.please_fill_in_all_fields));
            return;
        }

        //Creating a random document ID
        String documentId = generateRandomDocumentId(15);

        //Creating a new feedback record
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("name", name);
        feedback.put("phone", Long.parseLong(phone));
        feedback.put("email", email);
        feedback.put("comment", comment);
        feedback.put("rating", (long) rating);
        feedback.put("deviceModel", deviceModel);

        //Adding the feedback to the 'feedbackRecord' collection
        db.collection("feedbackRecord").document(documentId)
                .set(feedback)
                .addOnSuccessListener(aVoid -> DisplayToast(getString(R.string.feedback_submitted_successfully)))
                .addOnFailureListener(e -> DisplayToast(getString(R.string.failed_to_submit_feedback)));
    }

    private String generateRandomDocumentId(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private void DisplayToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
