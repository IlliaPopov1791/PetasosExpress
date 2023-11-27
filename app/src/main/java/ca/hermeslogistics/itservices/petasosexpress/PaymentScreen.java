package ca.hermeslogistics.itservices.petasosexpress;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class PaymentScreen extends Fragment {
    private Spinner monthSpinner, yearSpinner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_screen, container, false);

        TextView amountTextView = view.findViewById(R.id.amount);
        EditText creditCardNumber = view.findViewById(R.id.credit_card_number);
        EditText securityCode = view.findViewById(R.id.security_code);
        EditText cardHolderName = view.findViewById(R.id.card_holder_name);
        EditText zipCode = view.findViewById(R.id.zip_code);
        Button payButton = view.findViewById(R.id.pay_button);
        monthSpinner = view.findViewById(R.id.spinner_month);
        yearSpinner = view.findViewById(R.id.spinner_year);

        List<String> months = Arrays.asList(getResources().getStringArray(R.array.months));
        List<String> years = Arrays.asList(getResources().getStringArray(R.array.year_array));

        SpinnerAdapter monthAdapter = new SpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        SpinnerAdapter yearAdapter = new SpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        if (getArguments() != null) {
            String totalAmount = getArguments().getString("totalAmount", "0.00");
            amountTextView.setText(totalAmount);
        }

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePaymentDetails(creditCardNumber, securityCode, cardHolderName, zipCode)) {

                }
            }
        });

        return view;
    }

    private boolean validatePaymentDetails(EditText creditCardNumber, EditText securityCode, EditText cardHolderName, EditText zipCode) {
        if (creditCardNumber.getText().toString().length() != 16) {
            DisplayToast(getString(R.string.credit_card_number_must_be_16_digits));
            return false;
        }
        if (monthSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select an expiration month", Toast.LENGTH_LONG).show();
            return false;
        }

        if (yearSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select an expiration year", Toast.LENGTH_LONG).show();
            return false;
        }

        if (securityCode.getText().toString().length() != 3) {
            DisplayToast(getString(R.string.cvv_must_be_3_digits));
            return false;
        }

        if (cardHolderName.getText().toString().trim().isEmpty()) {
            DisplayToast(getString(R.string.card_holder_name_is_required));
            return false;
        }

        if (zipCode.getText().toString().length() != 6) {
            DisplayToast(getString(R.string.zip_code_must_be_6_characters));
            return false;
        }

        return true;
    }
    private void DisplayToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
