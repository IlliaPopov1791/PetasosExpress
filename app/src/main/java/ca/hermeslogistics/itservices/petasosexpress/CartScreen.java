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

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

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
        productAdapter = new ProductAdapter(requireContext(), productList);
        cartItemsListView.setAdapter(productAdapter);

        loadCartItems();

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
}