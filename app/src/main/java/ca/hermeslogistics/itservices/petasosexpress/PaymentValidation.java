package ca.hermeslogistics.itservices.petasosexpress;

public class PaymentValidation {

    protected static boolean validateCreditCardNumberLength(String creditCardNumber) {
        return creditCardNumber != null && creditCardNumber.length() == 16;
    }

    protected static boolean validateSecurityCodeLength(String securityCode) {
        return securityCode != null && securityCode.length() == 3;
    }

    protected static boolean validateCardHolderName(String cardHolderName) {
        return cardHolderName != null && !cardHolderName.trim().isEmpty();
    }

    protected static boolean validateAddress(String zipCode) {
        return zipCode != null && !zipCode.trim().isEmpty();
    }

    protected static boolean validateExpirationMonth(int selectedMonthPosition) {
        return selectedMonthPosition > 0;
    }

    protected static boolean validateExpirationYear(int selectedYearPosition) {
        return selectedYearPosition > 0;
    }
}
