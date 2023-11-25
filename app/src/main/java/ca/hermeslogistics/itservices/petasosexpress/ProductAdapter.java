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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    private boolean isForCartScreen;

    public ProductAdapter(Context context, List<Product> products, boolean isForCartScreen) {
        super(context, 0, products);
        this.isForCartScreen = isForCartScreen;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Product product = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView tvName = convertView.findViewById(android.R.id.text1);
        if (isForCartScreen) {
            tvName.setText(product.getCartName()); // Use cart name in cart screen
        } else {
            tvName.setText(product.getDisplayName()); // Use display name otherwise
        }

        return convertView;
    }
}

