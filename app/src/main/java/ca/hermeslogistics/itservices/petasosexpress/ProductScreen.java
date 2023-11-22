package ca.hermeslogistics.itservices.petasosexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class ProductScreen extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_screen, container, false);

        ImageView productImage = view.findViewById(R.id.product_image);
        TextView productNameTextView = view.findViewById(R.id.product_name);
        TextView productProducerTextView = view.findViewById(R.id.product_producer);
        TextView productPriceTextView = view.findViewById(R.id.product_price);
        Spinner quantitySpinner = view.findViewById(R.id.quantity_spinner);
        Button payButton = view.findViewById(R.id.pay_button);

        Bundle args = getArguments();
        if (args != null) {
            String productName = args.getString("productName");
            double productPrice = args.getDouble("productPrice");
            String productProducer = args.getString("productProducer");

            // Update UI with product data
            productNameTextView.setText(productName);
            productProducerTextView.setText(productProducer);
            productPriceTextView.setText(String.format(Locale.getDefault(), "$%.2f", productPrice));

        }


        return view;
    }

    private void processPayment(Product product, int quantity) {
    }
}
