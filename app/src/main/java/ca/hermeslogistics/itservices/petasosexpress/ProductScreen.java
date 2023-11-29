package ca.hermeslogistics.itservices.petasosexpress;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.StorageReference;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class ProductScreen extends Fragment {
    private FirebaseStorage storage;
    private int productId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_screen, container, false);

        ImageView productImage = view.findViewById(R.id.product_image);
        TextView productNameTextView = view.findViewById(R.id.product_name);
        TextView productProducerTextView = view.findViewById(R.id.product_producer);
        TextView productPriceTextView = view.findViewById(R.id.product_price);
        TextView quantity = view.findViewById(R.id.quantity);
        Button riseButton = view.findViewById(R.id.increase_button);
        Button dropButton = view.findViewById(R.id.decrease_button);
        Button cartButton = view.findViewById(R.id.cart_button);

        // Set the initial quantity
        int initialQuantity = 1;
        quantity.setText(String.valueOf(initialQuantity));

        storage = FirebaseStorage.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            String productName = args.getString("productName");
            double productPrice = args.getDouble("productPrice");
            String productProducer = args.getString("productProducer");
            productId = args.getInt("productId");

            // Update UI with product data
            productNameTextView.setText(productName);
            productProducerTextView.setText(productProducer);
            productPriceTextView.setText(String.format(Locale.getDefault(), "$%.2f", productPrice));
            loadProductImage(productImage, productId);
        }

        // Increase quantity
        riseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantity.getText().toString());
                currentQuantity++;
                quantity.setText(String.valueOf(currentQuantity));
            }
        });

        // Decrease quantity
        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantity.getText().toString());
                if (currentQuantity > 1) {
                    currentQuantity--;
                    quantity.setText(String.valueOf(currentQuantity));
                }
            }
        });

        // Add to the cart
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantity.getText().toString());
                addToCart(productId, currentQuantity);
            }
        });

        FloatingActionButton fabCart = view.findViewById(R.id.fab_cart);
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCartFragment();
            }
        });

        return view;
    }

    private void loadProductImage(final ImageView imageView, int productId) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("goodsImages/" + productId + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }


    private void addToCart(int productId, int quantity) {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        String cartJson = sharedPrefs.getString("cart", "[]");

        try {
            JSONArray cartArray = new JSONArray(cartJson);
            boolean productExists = false;

            //If the product already exists in the cart
            for (int i = 0; i < cartArray.length(); i++) {
                JSONObject cartItem = cartArray.getJSONObject(i);
                if (cartItem.getInt("id") == productId) {
                    //Updating quantity
                    int existingQuantity = cartItem.getInt("quantity");
                    cartItem.put("quantity", existingQuantity + quantity);
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                //Adding new product to cart
                JSONObject newCartItem = new JSONObject();
                newCartItem.put("id", productId);
                newCartItem.put("quantity", quantity);
                cartArray.put(newCartItem);
            }

            //Saving the updated cart back to SharedPreferences
            sharedPrefs.edit().putString("cart", cartArray.toString()).apply();
            //Snackbar
            View view = getActivity().findViewById(android.R.id.content);
            Snackbar.make(view, getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCartFragment() {
        CartScreen cartScreenFragment = new CartScreen();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, cartScreenFragment)
                .addToBackStack(null) // Optional: adds the transaction to the back stack
                .commit();
    }
}
