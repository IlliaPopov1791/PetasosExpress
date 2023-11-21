package ca.hermeslogistics.itservices.petasosexpress;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.content.SharedPreferences;
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
    private static final String PREFS_NAME = "FeedbackPrefs";
    private static final String LAST_FEEDBACK_KEY = "lastFeedbackTime";

    private FirebaseFirestore db;

    public FeedbackScreen() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load the appropriate layout based on orientation
        View view;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.feedback_screen, container, false);
        } else {
            view = inflater.inflate(R.layout.feedback_screen_landscape, container, false);
        }

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
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, 0);
        long lastFeedbackTime = prefs.getLong(LAST_FEEDBACK_KEY, 0);
        long currentTime = System.currentTimeMillis();

// Check if last feedback was sent less than 24 hours ago
        long timeElapsedSinceLastFeedback = currentTime - lastFeedbackTime;
        long twentyFourHoursInMilliseconds = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        if (timeElapsedSinceLastFeedback < twentyFourHoursInMilliseconds) {
            long timeLeft = twentyFourHoursInMilliseconds - timeElapsedSinceLastFeedback;

            // Convert milliseconds to hours and minutes
            long hoursLeft = timeLeft / (60 * 60 * 1000);
            long minutesLeft = (timeLeft % (60 * 60 * 1000)) / (60 * 1000);

            DisplayToast("Please wait " + hoursLeft + " hours and " + minutesLeft + " minutes before sending feedback again.");
            return;
        }

        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String comment = editTextComment.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            DisplayToast(getString(R.string.please_fill_in_all_fields));
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            DisplayToast(getString(R.string.invalid_email_format));
            return;
        }

        String cleanPhone = phone.replaceAll("[^0-9]", "");
        if (cleanPhone.length() != 10) {
            DisplayToast(getString(R.string.invalid_phone_format));
            return;
        }

        String documentId = generateRandomDocumentId(15);

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("name", name);
        feedback.put("phone", Long.parseLong(cleanPhone));
        feedback.put("email", email);
        feedback.put("comment", comment);
        feedback.put("rating", (long) rating);
        feedback.put("deviceModel", android.os.Build.MODEL);

        // Inform user that feedback is being processed
        DisplayToast("Processing feedback...");

        // Handler to introduce delay
        new Handler().postDelayed(() -> {
            // Save current time as last feedback time
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(LAST_FEEDBACK_KEY, currentTime);
            editor.apply();

            // Submit feedback...
            db.collection("feedbackRecord").document(documentId)
                    .set(feedback)
                    .addOnSuccessListener(aVoid -> DisplayToast(getString(R.string.feedback_submitted_successfully)))
                    .addOnFailureListener(e -> DisplayToast(getString(R.string.failed_to_submit_feedback)));
        }, 5000); // 5000 milliseconds delay
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
