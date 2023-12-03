package ca.hermeslogistics.itservices.petasosexpress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static ca.hermeslogistics.itservices.petasosexpress.PaymentValidation.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class PaymentValidationTest {

    @Test
    public void testValidCreditCardNumber() {
        assertTrue(validateCreditCardNumberLength("1234567890123456"));
    }

    @Test
    public void testInvalidCreditCardNumber() {
        assertFalse(validateCreditCardNumberLength("1234"));
    }


    @Test
    public void testValidExpirationMonth() {
        assertTrue(validateExpirationMonth(5)); //May
    }

    @Test
    public void testInvalidExpirationMonth() {
        assertFalse(validateExpirationMonth(0)); //'no selection'
    }

    @Test
    public void testValidExpirationYear() {
        assertTrue(validateExpirationYear(3)); //year 2023
    }

    @Test
    public void testInvalidExpirationYear() {
        assertFalse(validateExpirationYear(0)); //'no selection'
    }
    @Test
    public void testValidSecurityCode() {
        assertTrue(validateSecurityCodeLength("123"));
    }
    @Test
    public void testInvalidSecurityCode() {
        assertFalse(validateSecurityCodeLength("12"));
    }
    @Test
    public void testValidCardHolderName() {
        assertTrue(validateCardHolderName("John Doe"));
    }
    @Test
    public void testInvalidCardHolderName() {
        assertFalse(validateCardHolderName(""));
    }
    @Test
    public void testValidAddress() {
        assertTrue(validateAddress("Petasos 12345"));
    }
    @Test
    public void testInvalidAddress() {
        assertFalse(validateAddress(""));
    }
}
