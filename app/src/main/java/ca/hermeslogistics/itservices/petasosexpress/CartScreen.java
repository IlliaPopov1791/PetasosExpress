package ca.hermeslogistics.itservices.petasosexpress;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class CartScreen extends Fragment {
    private ListView cartItemsListView;
    private TextView totalAmountTextView;
    private Button checkoutButton;
    private ProductAdapter productAdapter;
    private List<Product> productList;


    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_screen, container, false);

        cartItemsListView = view.findViewById(R.id.cart_items_list);
        totalAmountTextView = view.findViewById(R.id.total_amount);
        checkoutButton = view.findViewById(R.id.checkout_button);
        db = FirebaseFirestore.getInstance();

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(requireContext(), productList, true);
        productAdapter.setOnUpdateListener(this::updateTotalAmount);
        cartItemsListView.setAdapter(productAdapter);

        loadCartItems();

        checkoutButton.setOnClickListener(v -> navigateToPaymentScreen());

        return view;
    }
    private void loadCartItems() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        String cartJson = sharedPrefs.getString("cart", "[]");
        AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);

        try {
            JSONArray cartArray = new JSONArray(cartJson);
            for (int i = 0; i < cartArray.length(); i++) {
                JSONObject cartItem = cartArray.getJSONObject(i);
                int productId = cartItem.getInt("id");
                int quantity = cartItem.getInt("quantity");

                String documentId = String.format("%012d", productId);

                db.collection("goods").document(documentId).get().addOnSuccessListener(documentSnapshot -> {
                    String name = documentSnapshot.getString("name");
                    Double price = documentSnapshot.getDouble("price");
                    String producer = documentSnapshot.getString("producer");
                    String type = documentSnapshot.getString("type");

                    if (name != null && price != null) {
                        Product product = new Product(name, productId, price, producer, type);
                        product.setQuantity(quantity);
                        productList.add(product);
                        productAdapter.notifyDataSetChanged();

                        //Calculating total amount
                        totalAmount.updateAndGet(v -> Double.valueOf(v + price * quantity));
                        totalAmountTextView.setText(String.format(Locale.getDefault(), "$%.2f", totalAmount.get()));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Method to update total amount
    public void updateTotalAmount() {
        double totalAmount = 0.0;
        for (Product product : productList) {
            totalAmount += product.getPrice() * product.getQuantity();
        }
        totalAmountTextView.setText(String.format(Locale.getDefault(), "$%.2f", totalAmount));
    }

    private void navigateToPaymentScreen() {
        String totalAmount = totalAmountTextView.getText().toString();

        // Check if the total amount is $0.00
        if (totalAmount.equals("$00.00")) {
            View view = getActivity().findViewById(android.R.id.content);
            Snackbar.make(view, getString(R.string.cart_empty_message), Snackbar.LENGTH_SHORT).show();
        } else {
            PaymentScreen paymentScreen = new PaymentScreen();

            Bundle args = new Bundle();
            args.putString("totalAmount", totalAmount);
            paymentScreen.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, paymentScreen)
                    .addToBackStack(null)
                    .commit();
        }
    }
}