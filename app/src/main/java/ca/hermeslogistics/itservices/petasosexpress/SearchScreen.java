package ca.hermeslogistics.itservices.petasosexpress;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchScreen extends Fragment {

    // UI elements
    private ListView searchResultsListView;
    private ProductAdapter productAdapter;
    private EditText searchEditText;
    private FloatingActionButton fabCart;

    // Firebase
    private FirebaseFirestore db;

    // Data lists
    private List<Product> productList;
    private List<Product> fullItemList;

    public SearchScreen() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_screen, container, false);

        // Initialize UI elements
        searchResultsListView = view.findViewById(R.id.search_list);
        searchEditText = view.findViewById(R.id.searchEditText);
        fabCart = view.findViewById(R.id.fab_cart);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize data lists and adapter
        productList = new ArrayList<>();
        fullItemList = new ArrayList<>();
        productAdapter = new ProductAdapter(requireContext(), productList, false);
        searchResultsListView.setAdapter(productAdapter);

        // Get initial search query from arguments
        Bundle args = getArguments();
        String initialQuery = args != null ? args.getString("searchQuery", "") : "";
        searchEditText.setText(initialQuery);

        // Fetch items and set up listeners
        fetchItems(initialQuery);
        setUpTextWatcher();
        setUpEditorActionListener();
        setUpListViewClickListener();

        return view;
    }

    // Fetch items from Firestore
    private void fetchItems(String initialQuery) {
        db.collection("goods").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Clear lists
                productList.clear();
                fullItemList.clear();

                // Parse Firestore documents
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    Long id = document.getLong("id");
                    Double price = document.getDouble("price");
                    String producer = document.getString("producer");
                    String type = document.getString("type");

                    // Check for null values
                    if (name != null && price != null) {
                        Product product = new Product(name, id.intValue(), price, producer, type);
                        fullItemList.add(product);
                        productList.add(product);
                    }
                }

                // Notify adapter
                productAdapter.notifyDataSetChanged();

                // Filter items based on initial query
                if (!initialQuery.isEmpty()) {
                    filterAdapter(initialQuery);
                }
            } else {
                Log.e("FetchItems", "Error fetching documents", task.getException());
            }
        });
    }

    // Filter adapter based on the search query
    private void filterAdapter(String query) {
        query = query.toLowerCase(Locale.getDefault());
        List<Product> filteredList = new ArrayList<>();

        for (Product product : fullItemList) {
            if (product.matchesQuery(query)) {
                filteredList.add(product);
            }
        }

        productAdapter.clear();
        productAdapter.addAll(filteredList);
        productAdapter.notifyDataSetChanged();
    }

    // Close the soft keyboard
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    // Navigate to the ProductScreen fragment
    private void navigateToProductScreen(Product product) {
        ProductScreen productScreen = new ProductScreen();

        Bundle args = new Bundle();
        args.putString("productName", product.getName());
        args.putDouble("productPrice", product.getPrice());
        args.putString("productProducer", product.getProducer());
        args.putInt("productId", product.getId());

        productScreen.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, productScreen)
                .addToBackStack(null)
                .commit();
    }

    // Load the CartScreen fragment
    private void loadCartFragment() {
        CartScreen cartScreenFragment = new CartScreen();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, cartScreenFragment)
                .addToBackStack(null)
                .commit();
    }

    // Set up text change listener for searchEditText
    private void setUpTextWatcher() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterAdapter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    // Set up editor action listener for searchEditText
    private void setUpEditorActionListener() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                filterAdapter(query);
                closeKeyboard();
                return true;
            }
            return false;
        });
    }

    // Set up click listener for searchResultsListView items
    private void setUpListViewClickListener() {
        searchResultsListView.setOnItemClickListener((adapterView, view1, position, id) -> {
            Product selectedProduct = productAdapter.getItem(position);
            navigateToProductScreen(selectedProduct);
        });
    }

    // Set up click listener for the FloatingActionButton (Cart)
    private void setUpFabCartClickListener() {
        fabCart.setOnClickListener(view -> {
            loadCartFragment();
        });
    }
}
