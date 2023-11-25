package ca.hermeslogistics.itservices.petasosexpress;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProductAdapter extends ArrayAdapter<Product> {
    private boolean isForCartScreen;
    private Runnable onUpdateListener;

    public ProductAdapter(Context context, List<Product> products, boolean isForCartScreen) {
        super(context, 0, products);
        this.isForCartScreen = isForCartScreen;
    }

    public void setOnUpdateListener(Runnable onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Product product = getItem(position);

        if (isForCartScreen) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cart_item_layout, parent, false);
            }
            TextView tvCartItem = convertView.findViewById(R.id.tvCartItem);
            Button btnRemove = convertView.findViewById(R.id.btnRemove);
            tvCartItem.setText(product.getCartName());

            btnRemove.setOnClickListener(v -> removeItemFromCart(product.getId(), position));
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView tvName = convertView.findViewById(android.R.id.text1);
            tvName.setText(product.getDisplayName());
        }

        return convertView;
    }

    private void removeItemFromCart(int productId, int position) {
        SharedPreferences sharedPrefs = getContext().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        String cartJson = sharedPrefs.getString("cart", "[]");

        try {
            JSONArray cartArray = new JSONArray(cartJson);
            JSONArray newCartArray = new JSONArray();

            for (int i = 0; i < cartArray.length(); i++) {
                JSONObject cartItem = cartArray.getJSONObject(i);
                if (cartItem.getInt("id") != productId) {
                    newCartArray.put(cartItem);
                }
            }

            sharedPrefs.edit().putString("cart", newCartArray.toString()).apply();
            this.remove(getItem(position));
            this.notifyDataSetChanged();

            // Notify the CartScreen to update the total amount
            if (onUpdateListener != null) {
                onUpdateListener.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

