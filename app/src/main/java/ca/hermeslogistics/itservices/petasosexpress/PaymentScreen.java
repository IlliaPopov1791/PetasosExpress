package ca.hermeslogistics.itservices.petasosexpress;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class PaymentScreen extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_screen, container, false);

        TextView amountTextView = view.findViewById(R.id.amount);

        if (getArguments() != null) {
            String totalAmount = getArguments().getString("totalAmount", "0.00");
            amountTextView.setText(totalAmount);
        }


        return view;
    }
}
