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

import com.google.firebase.storage.FirebaseStorage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.StorageReference;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Locale;

public class ProductScreen extends Fragment {
    private FirebaseStorage storage;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_screen, container, false);

        ImageView productImage = view.findViewById(R.id.product_image);
        TextView productNameTextView = view.findViewById(R.id.product_name);
        TextView productProducerTextView = view.findViewById(R.id.product_producer);
        TextView productPriceTextView = view.findViewById(R.id.product_price);
        Button riseButton = view.findViewById(R.id.increase_button);
        Button dropButton = view.findViewById(R.id.decrease_button);
        Button payButton = view.findViewById(R.id.pay_button);

        storage = FirebaseStorage.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            String productName = args.getString("productName");
            double productPrice = args.getDouble("productPrice");
            String productProducer = args.getString("productProducer");
            int productId = args.getInt("productId");

            // Update UI with product data
            productNameTextView.setText(productName);
            productProducerTextView.setText(productProducer);
            productPriceTextView.setText(String.format(Locale.getDefault(), "$%.2f", productPrice));
            loadProductImage(productImage, productId);
        }


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


    private void processPayment(Product product, int quantity) {
    }
}
