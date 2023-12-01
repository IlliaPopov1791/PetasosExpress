package ca.hermeslogistics.itservices.petasosexpress;

public class ValidationUtils {
    //Method to validate email
    protected static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Method to validate phone number
    protected static boolean isValidPhone(String phone) {
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        String phonePattern = "^[0-9]{10}$";
        return cleanPhone.matches(phonePattern);
    }

    //Method to validate password
    protected static boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
        return password.matches(passwordPattern);
    }

    public static boolean isValidRating(float rating) {
        return rating >= 1.0f && rating <= 5.0f;
    }

    public static boolean isValidComment(String comment) {
        return comment != null && !comment.trim().isEmpty();
    }
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }
}
