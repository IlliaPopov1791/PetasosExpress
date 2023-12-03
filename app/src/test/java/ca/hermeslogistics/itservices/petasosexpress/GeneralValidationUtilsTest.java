package ca.hermeslogistics.itservices.petasosexpress;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class GeneralValidationUtilsTest {

    @Test
    public void testIsValidName_ValidName() {
        assertTrue(GeneralValidationUtils.isValidName("John Doe"));
    }

    @Test
    public void testIsValidName_InvalidName_Empty() {
        assertFalse(GeneralValidationUtils.isValidName(""));
    }

    @Test
    public void testIsValidPhoneNumber_ValidPhoneNumber() {
        assertTrue(GeneralValidationUtils.isValidPhone("1234567890"));
    }

    @Test
    public void testIsValidPhoneNumber_InvalidPhoneNumber() {
        assertFalse(GeneralValidationUtils.isValidPhone("12345"));
    }

    @Test
    public void testIsValidRating_ValidRating() {
        assertTrue(GeneralValidationUtils.isValidRating(3.5f));
    }

    @Test
    public void testIsValidRating_InvalidRating() {
        assertFalse(GeneralValidationUtils.isValidRating(0f));
    }

    @Test
    public void testIsValidComment_ValidComment() {
        assertTrue(GeneralValidationUtils.isValidComment("This is a valid comment."));
    }

    @Test
    public void testIsValidComment_InvalidComment_Empty() {
        assertFalse(GeneralValidationUtils.isValidComment(""));
    }
}
