package ca.hermeslogistics.itservices.petasosexpress;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class PaymentScreen extends Fragment {
    private Spinner monthSpinner, yearSpinner;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String totalAmount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;

        // Set the layout based on the orientation
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.payment_screen, container, false);
        } else {
            view = inflater.inflate(R.layout.payment_screen_landscape, container, false);
        }
        //Firebase components
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //UI components
        TextView amountTextView = view.findViewById(R.id.amount);
        EditText creditCardNumber = view.findViewById(R.id.credit_card_number);
        EditText securityCode = view.findViewById(R.id.security_code);
        EditText cardHolderName = view.findViewById(R.id.card_holder_name);
        EditText address = view.findViewById(R.id.zip_code);
        Button payButton = view.findViewById(R.id.pay_button);
        monthSpinner = view.findViewById(R.id.spinner_month);
        yearSpinner = view.findViewById(R.id.spinner_year);

        SharedPreferences settings = getActivity().getSharedPreferences(AppSettings.PREFS_NAME, Context.MODE_PRIVATE);
        String savedAddress = settings.getString(AppSettings.ADDRESS_KEY, "");
        address.setText(savedAddress);

        List<String> months = Arrays.asList(getResources().getStringArray(R.array.months));
        List<String> years = Arrays.asList(getResources().getStringArray(R.array.year_array));

        SpinnerAdapter monthAdapter = new SpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        SpinnerAdapter yearAdapter = new SpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        if (getArguments() != null) {
            this.totalAmount = getArguments().getString("totalAmount", "0.00");
            amountTextView.setText(this.totalAmount);
        }

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePaymentDetails(creditCardNumber, securityCode, cardHolderName, address)) {
                    createOrder(address.getText().toString(), totalAmount);
                    navigateToCartScreen();
                }
            }
        });
        return view;
    }

    private void createOrder(String address, String totalAmount) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                //Fetch cart items
                SharedPreferences sharedPrefs = getActivity().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
                String cartJson = sharedPrefs.getString("cart", "[]");

                try {
                    JSONArray cartArray = new JSONArray(cartJson);
                    Map<String, Object> orderMap = new HashMap<>();
                    for (int i = 0; i < cartArray.length(); i++) {
                        JSONObject cartItem = cartArray.getJSONObject(i);
                        int productId = cartItem.getInt("id");
                        int quantity = cartItem.getInt("quantity");
                        orderMap.put(String.valueOf(productId), quantity);
                    }

                    //Prepare order document
                    Map<String, Object> orderDocument = new HashMap<>();
                    orderDocument.put("Address", address);
                    orderDocument.put("User", userEmail);
                    orderDocument.put("order", orderMap);
                    orderDocument.put("status", "ordered");
                    orderDocument.put("timestamp", new Date());

                    String numericTotalAmount = totalAmount.replaceAll("[^\\d.]", "");
                    double total = Double.parseDouble(numericTotalAmount);
                    orderDocument.put("total", total);

                    submitOrder(orderDocument);
                    clearCart(sharedPrefs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void submitOrder(Map<String, Object> orderDocument) {
        String documentId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        db.collection("orderRecord").document(documentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                //If exists retry
                submitOrder(orderDocument);
            } else {
                //Submit a new order
                db.collection("orderRecord").document(documentId)
                        .set(orderDocument)
                        .addOnSuccessListener(aVoid -> DisplayToast(getString(R.string.order_created_successfully)))
                        .addOnFailureListener(e -> DisplayToast(getString(R.string.error_creating_order)));

                setDeliveryReference(documentId);
            }
        });
    }


    private void setDeliveryReference(String orderDocumentId) {
        db.collection("PetasosRecord")
                .whereEqualTo("status", "ready")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentReference deliveryRef = task.getResult().getDocuments().get(0).getReference();
                            db.collection("orderRecord").document(orderDocumentId)
                                    .update("delivery", deliveryRef)
                                    .addOnSuccessListener(aVoid -> {
                                        deliveryRef.update("status", "delivery");
                                    });
                        } else {
                            //No 'ready' Petasos found
                            db.collection("orderRecord").document(orderDocumentId)
                                    .update("status", "in a queue");
                            showQueueAlertDialog();
                        }
                    }
                });
    }

    private void showQueueAlertDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.queue_alert_title))
                .setMessage(getString(R.string.queue_alert_message))
                .setPositiveButton(getString(R.string.ok), null)
                .setIcon(R.drawable.ic_cart_foreground)
                .show();
    }


    private boolean validatePaymentDetails(EditText creditCardNumber, EditText securityCode, EditText cardHolderName, EditText zipCode) {
        //Needed to change again, otherwise it won't show toasts
        if (!PaymentValidation.validateCreditCardNumberLength(creditCardNumber.getText().toString())) {
            DisplayToast(getString(R.string.credit_card_number_must_be_16_digits));
            return false;
        }

        if (!PaymentValidation.validateExpirationMonth(monthSpinner.getSelectedItemPosition())) {
            DisplayToast(getString(R.string.please_select_an_expiration_month));
            return false;
        }

        if (!PaymentValidation.validateExpirationYear(yearSpinner.getSelectedItemPosition())) {
            DisplayToast(getString(R.string.please_select_an_expiration_year));
            return false;
        }

        if (!PaymentValidation.validateSecurityCodeLength(securityCode.getText().toString())) {
            DisplayToast(getString(R.string.cvv_must_be_3_digits));
            return false;
        }

        if (!PaymentValidation.validateCardHolderName(cardHolderName.getText().toString())) {
            DisplayToast(getString(R.string.card_holder_name_is_required));
            return false;
        }

        if (!PaymentValidation.validateAddress(zipCode.getText().toString())) {
            DisplayToast(getString(R.string.address_must_be_filled));
            return false;
        }

        return true;
    }

    private void clearCart(SharedPreferences sharedPrefs) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("cart", "[]");
        editor.apply();
    }
    private void DisplayToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void navigateToCartScreen() {
        Fragment cartScreenFragment = new CartScreen();
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, cartScreenFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
