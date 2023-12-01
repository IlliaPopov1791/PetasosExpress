package ca.hermeslogistics.itservices.petasosexpress;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FeedbackScreenTest {

    @Test
    public void testIsValidName_ValidName() {
        assertTrue(ValidationUtils.isValidName("John Doe"));
    }

    @Test
    public void testIsValidName_InvalidName_Empty() {
        assertFalse(ValidationUtils.isValidName(""));
    }

    @Test
    public void testIsValidPhoneNumber_ValidPhoneNumber() {
        assertTrue(ValidationUtils.isValidPhone("1234567890"));
    }

    @Test
    public void testIsValidPhoneNumber_InvalidPhoneNumber() {
        assertFalse(ValidationUtils.isValidPhone("12345"));
    }

    @Test
    public void testIsValidEmail_ValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
    }

    @Test
    public void testIsValidEmail_InvalidEmail() {
        assertFalse(ValidationUtils.isValidEmail("test@example"));
    }

    @Test
    public void testIsValidRating_ValidRating() {
        assertTrue(ValidationUtils.isValidRating(3.5f));
    }

    @Test
    public void testIsValidRating_InvalidRating() {
        assertFalse(ValidationUtils.isValidRating(0f));
    }

    @Test
    public void testIsValidComment_ValidComment() {
        assertTrue(ValidationUtils.isValidComment("This is a valid comment."));
    }

    @Test
    public void testIsValidComment_InvalidComment_Empty() {
        assertFalse(ValidationUtils.isValidComment(""));
    }
}
